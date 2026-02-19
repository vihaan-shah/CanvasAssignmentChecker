# Canvas Assignment Checker

A Java program that automatically checks your Canvas LMS assignments and notifies you about which assignments are due today and whether they've been submitted.

## Features

- ✅ Checks all active Canvas courses
- ✅ Identifies assignments due today
- ✅ Shows submission status (submitted vs. not submitted)
- ✅ Secure API token storage (not hardcoded in code)
- ✅ Optional email notifications via Gmail
- ✅ Can be scheduled to run automatically with cron

## Requirements

- **Java 11 or higher** ([Download here](https://adoptium.net/))
- **org.json library** ([Download here](https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar))
- **Canvas API Token** (generated from your Canvas account)
- **Python 3** (optional, for email notifications)

## Quick Start

### 1. Get Your Canvas API Token

1. Log into your Canvas account
2. Go to **Account** → **Settings**
3. Scroll to **Approved Integrations**
4. Click **+ New Access Token**
5. Set a purpose (e.g., "Assignment Checker")
6. Click **Generate Token**
7. **Copy the token immediately** (you won't see it again!)

### 2. Download Dependencies

Download the JSON library:
```bash
curl -O https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar
```

Or download it manually from the link above and place it in your project directory.

### 3. Configure the Program

**Update Canvas URL in `CanvasAssignmentChecker.java`:**

Open the file and find this line:
```java
private static final String CANVAS_URL = "https://canvas.instructure.com";
```

Change it to your school's Canvas URL, for example:
- `https://canvas.instructure.com` (if using main Canvas)
- `https://yourschool.instructure.com`
- `https://canvas.yourschool.edu`

**Create your API token file:**
```bash
echo "YOUR_API_TOKEN_HERE" > canvas_token.txt
chmod 600 canvas_token.txt  # Make it readable only by you
```

Replace `YOUR_API_TOKEN_HERE` with the actual token from step 1.

### 4. Compile

**On Mac/Linux:**
```bash
javac -cp ".:json-20231013.jar" CanvasAssignmentChecker.java
```

**On Windows:**
```bash
javac -cp ".;json-20231013.jar" CanvasAssignmentChecker.java
```

### 5. Run

**On Mac/Linux:**
```bash
java -cp ".:json-20231013.jar" CanvasAssignmentChecker canvas_token.txt
```

**On Windows:**
```bash
java -cp ".;json-20231013.jar" CanvasAssignmentChecker canvas_token.txt
```

## Example Output

```
=== Canvas Assignment Checker ===
Checking assignments for: 2026-02-18

✅ SUBMITTED: AP Computer Science - Java Practice Problems
❌ NOT SUBMITTED: English Literature - Essay Draft
✅ SUBMITTED: Calculus - Homework 5

⚠️  You have unsubmitted assignments due today!
```

## Optional: Email Notifications

To receive daily email notifications:

### Setup Gmail Notifications

1. **Enable 2-Step Verification** on your Google account
2. **Generate an App Password:**
   - Go to https://myaccount.google.com/apppasswords
   - Create an app password for "Mail"
   - Copy the 16-character password

3. **Configure `send_email.py`:**
   ```python
   GMAIL_USER = "your-email@gmail.com"
   GMAIL_APP_PASSWORD = "your-16-char-app-password"
   TO_EMAIL = "your-email@gmail.com"
   ```

4. **Test it:**
   ```bash
   java -cp ".:json-20231013.jar" CanvasAssignmentChecker canvas_token.txt | python3 send_email.py
   ```

## Automatic Daily Checks with Cron

To run the checker automatically every day at 7:30 PM:

1. **Edit your crontab:**
   ```bash
   crontab -e
   ```

2. **Add this line** (update the path to match your setup):
   ```bash
   30 19 * * * cd /path/to/your/project && java -cp ".:json-20231013.jar" CanvasAssignmentChecker canvas_token.txt | python3 send_email.py
   ```

   Or without email notifications:
   ```bash
   30 19 * * * cd /path/to/your/project && java -cp ".:json-20231013.jar" CanvasAssignmentChecker canvas_token.txt >> ~/canvas_log.txt 2>&1
   ```

3. **Save and exit**

### Cron Time Examples

- `30 19 * * *` = 7:30 PM daily
- `0 8 * * *` = 8:00 AM daily
- `0 20 * * 1-5` = 8:00 PM Monday-Friday only

## Security Best Practices

🔒 **IMPORTANT:** Never commit your API token or Gmail credentials to version control!

**Add to `.gitignore`:**
```
canvas_token.txt
send_email.py
*.class
```

Or create a separate config file for credentials:
```bash
# config.txt (add to .gitignore)
your-canvas-token-here
your-email@gmail.com
your-app-password-here
```

## Customization

### Check assignments due on a different date

Modify the `isDueToday()` method in `CanvasAssignmentChecker.java`:

**For tomorrow:**
```java
return dueDateLocal.equals(LocalDate.now().plusDays(1));
```

**For a specific date:**
```java
LocalDate targetDate = LocalDate.of(2026, 2, 20);
return dueDateLocal.equals(targetDate);
```

**For the next 7 days:**
```java
LocalDate today = LocalDate.now();
LocalDate weekFromNow = today.plusDays(7);
return !dueDateLocal.isBefore(today) && !dueDateLocal.isAfter(weekFromNow);
```

After making changes, recompile the program.

## Troubleshooting

### "401 Unauthorized" Error
- Your API token is invalid or expired
- Generate a new token from Canvas
- Make sure your `canvas_token.txt` has only the token (no extra spaces)
- Verify your Canvas URL is correct

### "ClassNotFoundException: org.json.JSONObject"
- Make sure you're using the `-cp` flag with the JSON jar file
- Verify the jar file name matches exactly (case-sensitive)

### Email not sending
- Check that 2-Step Verification is enabled on your Google account
- Make sure you're using an App Password, not your regular password
- Check spam folder
- Verify Gmail credentials in `send_email.py`

### Cron job not running
- Check cron is running: `ps aux | grep cron`
- View cron logs: `tail -f /var/log/syslog` (Linux) or check Console.app (Mac)
- Make sure paths are absolute (not relative) in crontab
- Test the command manually first

## File Structure

```
canvas-assignment-checker/
├── CanvasAssignmentChecker.java   # Main program
├── send_email.py                  # Email notification script
├── canvas_token.txt              # Your API token (DO NOT COMMIT)
├── json-20231013.jar             # JSON library dependency
├── README.md                      # This file
└── .gitignore                     # Git ignore file
```

## Contributing

Feel free to fork this project and submit pull requests for improvements!

## License

MIT License - feel free to use and modify as needed.

## Acknowledgments

- Uses the [Canvas LMS REST API](https://canvas.instructure.com/doc/api/)
- JSON parsing with [org.json](https://github.com/stleary/JSON-java)

---

**Happy studying! 📚**
