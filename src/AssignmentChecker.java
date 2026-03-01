import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class AssignmentChecker {

    // TODO: Replace with your actual Canvas URL
    private static final String CANVAS_URL = "https://esuhsd.instructure.com";
    private static String API_TOKEN; // Will be loaded from file
    private static final ZoneId PST_ZONE = ZoneId.of("America/Los_Angeles");

    public static void main(String[] args) {
        try {
            // Check if API token file path was provided
            if (args.length == 0) {
                System.err.println("Error: Please provide the path to your API token file");
                System.err.println("Usage: java AssignmentChecker <path-to-token-file>");
                System.exit(1);
            }

            // Load API token from file
            String tokenFilePath = args[0];
            try {
                API_TOKEN = Files.readString(Paths.get(tokenFilePath)).trim();
                if (API_TOKEN.isEmpty()) {
                    System.err.println("Error: Token file is empty");
                    System.exit(1);
                }
            } catch (IOException e) {
                System.err.println("Error: Could not read token file at " + tokenFilePath);
                System.err.println("Make sure the file exists and you have permission to read it");
                System.exit(1);
            }

            System.out.println("=== Canvas Assignment Checker ===");
            LocalDate todayPST = LocalDate.now(PST_ZONE);
            System.out.println("Checking assignments due today: " + todayPST + " (PST)");
            System.out.println();

            // Get all active courses
            JSONArray courses = getActiveCourses();

            boolean hasUnsubmitted = false;
            int totalAssignmentsFound = 0;

            // Check each course for assignments due in next 3 days
            for (int i = 0; i < courses.length(); i++) {
                JSONObject course = courses.getJSONObject(i);

                // Skip courses without a name or ID
                if (!course.has("name") || !course.has("id")) {
                    continue;
                }

                String courseName = course.optString("name", "Unnamed Course");
                int courseId = course.getInt("id");

                try {
                    // Get assignments for this course
                    JSONArray assignments = getAssignments(courseId);

                    // Check for assignments due in the next 3 days
                    for (int j = 0; j < assignments.length(); j++) {
                        JSONObject assignment = assignments.getJSONObject(j);

                        if (isDueToday(assignment)) {
                            totalAssignmentsFound++;
                            String assignmentName = assignment.optString("name", "Unnamed Assignment");
                            LocalDate dueDate = getDueDate(assignment);
                            boolean isSubmitted = checkIfSubmitted(courseId, assignment.getInt("id"));

                            // Always show the actual date for clarity
                            String dueDateStr = dueDate.format(DateTimeFormatter.ofPattern("MMM dd"));

                            if (!isSubmitted) {
                                System.out.println("❌ NOT SUBMITTED: " + courseName + " - " + assignmentName + " (Due: " + dueDateStr + ")");
                                hasUnsubmitted = true;
                            } else {
                                System.out.println("✅ SUBMITTED: " + courseName + " - " + assignmentName + " (Due: " + dueDateStr + ")");
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("⚠️  Error checking assignments for course: " + courseName);
                    System.err.println("   " + e.getMessage());
                }
            }

            System.out.println();
            System.out.println("Total assignments found due today: " + totalAssignmentsFound);

            if (totalAssignmentsFound == 0) {
                System.out.println("🎉 No assignments due today!");
            } else if (!hasUnsubmitted) {
                System.out.println("🎉 All assignments due today have been submitted!");
            } else {
                System.out.println("⚠️  You have unsubmitted assignments due today!");
            }

        } catch (Exception e) {
            System.err.println("Error checking assignments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static JSONArray getActiveCourses() throws Exception {
        // Get all courses (not just active enrollment)
        // This ensures we don't miss any courses
        String endpoint = CANVAS_URL + "/api/v1/courses?per_page=100";
        String response = makeAPIRequest(endpoint);
        JSONArray allCourses = new JSONArray(response);

        // Filter for courses that are likely current
        JSONArray activeCourses = new JSONArray();
        for (int i = 0; i < allCourses.length(); i++) {
            JSONObject course = allCourses.getJSONObject(i);

            // Skip courses with certain workflow states
            String workflowState = course.optString("workflow_state", "");
            if (workflowState.equals("deleted") || workflowState.equals("completed")) {
                continue;
            }

            // Add course to active list
            activeCourses.put(course);
        }

        return activeCourses;
    }

    private static JSONArray getAssignments(int courseId) throws Exception {
        String endpoint = CANVAS_URL + "/api/v1/courses/" + courseId + "/assignments?per_page=100";
        String response = makeAPIRequest(endpoint);
        return new JSONArray(response);
    }

    private static boolean checkIfSubmitted(int courseId, int assignmentId) throws Exception {
        String endpoint = CANVAS_URL + "/api/v1/courses/" + courseId +
                "/assignments/" + assignmentId + "/submissions/self";
        String response = makeAPIRequest(endpoint);
        JSONObject submission = new JSONObject(response);

        // Check if the assignment has been submitted
        String workflowState = submission.optString("workflow_state", "");
        return workflowState.equals("submitted") || workflowState.equals("graded");
    }

    private static boolean isDueToday(JSONObject assignment) {
        try {
            if (assignment.isNull("due_at")) {
                return false;
            }

            String dueAtStr = assignment.getString("due_at");
            ZonedDateTime dueDateTime = ZonedDateTime.parse(dueAtStr);

            // Convert to PST timezone
            ZonedDateTime dueDateTimePST = dueDateTime.withZoneSameInstant(PST_ZONE);
            LocalDate dueDateLocal = dueDateTimePST.toLocalDate();

            // Check if due TODAY (PST)
            LocalDate todayPST = LocalDate.now(PST_ZONE);

            return dueDateLocal.equals(todayPST);
        } catch (Exception e) {
            return false;
        }
    }

    private static LocalDate getDueDate(JSONObject assignment) {
        try {
            if (!assignment.isNull("due_at")) {
                String dueAtStr = assignment.getString("due_at");
                ZonedDateTime dueDateTime = ZonedDateTime.parse(dueAtStr);

                // Convert to PST timezone
                ZonedDateTime dueDateTimePST = dueDateTime.withZoneSameInstant(PST_ZONE);
                return dueDateTimePST.toLocalDate();
            }
        } catch (Exception e) {
            // Return a far future date if parsing fails
        }
        return LocalDate.now(PST_ZONE).plusYears(100);
    }

    private static String makeAPIRequest(String endpoint) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Bearer " + API_TOKEN)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("API request failed with status: " + response.statusCode());
        }

        return response.body();
    }
}