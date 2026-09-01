# 📚 Canvas Assignment Checker

A lightweight Java tool that checks your Canvas LMS courses every day and tells you exactly which assignments are due **today** and whether you've submitted them — delivered straight to your (and your parents') inbox.

No more forgetting an assignment because it was buried in a course you don't check often.

---

## Why I Built This

Canvas doesn't make it easy to see, at a glance, "what's due today across ALL my classes and have I turned it in?" This tool solves that by:

- Pulling every active course from the Canvas API
- Checking each one for assignments due **today** (correctly handling PST timezones, so 11:59 PM deadlines aren't mislabeled)
- Reporting submission status for each one
- Optionally emailing the results to you and/or your parents automatically every day

---

## What It Does

| Step | Description |
|------|--------------|
| 1️⃣ | Connects to Canvas using your personal API token |
| 2️⃣ | Loops through all of your active courses |
| 3️⃣ | Finds assignments due **today** (PST) |
| 4️⃣ | Checks whether each one has been submitted |
| 5️⃣ | Prints a clean ✅ / ❌ report |
| 6️⃣ | *(Optional)* Emails the report to multiple recipients |
| 7️⃣ | *(Optional)* Runs automatically every evening via `cron` |

---

## Sample Output

```
=== Canvas Assignment Checker ===
Checking assignments due today: 2026-02-28 (PST)

❌ NOT SUBMITTED: P-CALCULUS BC/AP - Section 9.2: p. 605, #45-57 (odd)
✅ SUBMITTED: P-COMP SCI A/AP - Lab 5: Recursion

Total assignments found due today: 2
⚠️  You have unsubmitted assignments due today!
```

---

## Project Structure

```
canvas-assignment-checker/
├── AssignmentChecker.java     # Core checker — talks to the Canvas API
├── send_email.py              # Sends the results to Gmail
├── config.json                # Your personal settings (gitignored)
├── config.example.json        # Template for config.json
├── canvas_token.txt           # Your Canvas API token (gitignored)
├── gmail_app_password.txt     # Your Gmail app password (gitignored)
├── json-20231013.jar          # JSON parsing library dependency
├── SETUP.md                   # 👉 Full step-by-step setup instructions
└── .gitignore
```

---

## Getting Started

This README is just the overview. For the full walkthrough — generating a Canvas token, installing Java, getting a Gmail App Password, configuring `config.json`, and scheduling it with `cron` — head to:

➡️ **[SETUP.md](./SETUP.md)**

---

## Tech Stack

- **Java 11+** — core assignment-checking logic, uses `java.net.http` for API calls
- **org.json** — lightweight JSON parsing
- **Python 3** — sends the email report via Gmail SMTP
- **Canvas LMS REST API** — data source
- **cron** — daily automation

---

## Security Notes

This project keeps secrets **out of source code**:
- Canvas API token → `canvas_token.txt`
- Gmail App Password → `gmail_app_password.txt`
- Personal settings (Canvas URL, email addresses) → `config.json`

All of these are excluded from version control via `.gitignore`. If you fork this repo, use `config.example.json` as your template and never commit your real config.

---

## Future Ideas

- [ ] Check assignments due in a custom date range (not just today)
- [ ] Slack or text message notifications instead of email
- [ ] Web dashboard instead of terminal/email output
- [ ] Support for multiple Canvas accounts (e.g., siblings)

---

## License

MIT — use it, modify it, share it.
