# Canvas Assignment Checker - Setup & Configuration

## Overview

This project now uses external configuration files to manage sensitive information like API tokens and email credentials. **No secrets are stored in the repository.**

## Initial Setup

### 1. Create Configuration File

Copy the example configuration:
```bash
cp config.example.json config.json
```

Edit `config.json` with your actual settings:
```json
{
  "canvas": {
    "url": "https://your-canvas-instance.instructure.com",
    "tokenFilePath": "./canvas_token.txt"
  },
  "email": {
    "gmailUser": "your-email@gmail.com",
    "appPasswordFilePath": "./gmail_app_password.txt",
    "recipients": [
      "your-email@gmail.com",
      "other-recipient@gmail.com"
    ]
  },
  "timezone": "America/Los_Angeles"
}
```

### 2. Get Canvas API Token

1. Log in to your Canvas instance
2. Go to **Settings** → **Approved Integrations**
3. Click **+ New Access Token**
4. Generate a token with appropriate scopes
5. Save it to a file:
```bash
echo "YOUR_CANVAS_API_TOKEN_HERE" > canvas_token.txt
```

### 3. Set Up Gmail (Optional - for email notifications)

1. Enable 2-Factor Authentication on your Google Account
2. Generate an App Password:
   - Go to https://myaccount.google.com/apppasswords
   - Select "Mail" and "Windows Computer" (or your device)
   - Google will generate a 16-character password
3. Save it to a file:
```bash
echo "YOUR_16_CHARACTER_APP_PASSWORD" > gmail_app_password.txt
```

## Running the Application

### Compile (if needed)
```bash
cd src
javac -cp json-20231013.jar AssignmentChecker.java
```

### Check assignments only
```bash
java -cp src/json-20231013.jar:. AssignmentChecker config.json canvas_token.txt
```

### With email notification
```bash
java -cp src/json-20231013.jar:. AssignmentChecker config.json canvas_token.txt | python3 src/send_email.py
```

### Set up as cron job (daily at 8 AM)
```bash
0 8 * * * cd /path/to/CanvasAssignmentChecker && java -cp src/json-20231013.jar:. AssignmentChecker config.json canvas_token.txt | python3 src/send_email.py
```

## File Structure

```
CanvasAssignmentChecker/
├── config.example.json          # Template (committed to repo)
├── config.json                  # Your actual config (git-ignored)
├── canvas_token.txt             # Your Canvas token (git-ignored)
├── gmail_app_password.txt       # Your Gmail app password (git-ignored)
├── .gitignore                   # Prevents accidental commits
├── src/
│   ├── AssignmentChecker.java   # Main Java program
│   ├── send_email.py            # Email notification script
│   └── json-20231013.jar        # JSON library
└── SETUP.md                     # This file
```

## Security

✅ **Credentials NOT in repository** - Only template files are committed  
✅ **Protected by .gitignore** - Prevents accidental commits of secrets  
✅ **Each developer has separate config** - Easy local testing  
✅ **Centralized configuration** - All settings in one place  
✅ **Best practices** - Uses app passwords, not regular passwords  

## Troubleshooting

### Config file not found
- Make sure you've copied `config.example.json` to `config.json`
- Check that the file is in the same directory as the Java program

### Token file not found
- Verify the path in `config.json` is correct
- Check that `canvas_token.txt` exists

### Authentication failed
- Verify your Canvas token is valid and hasn't expired
- Try generating a new token from Canvas settings

### Email not sending
- Check that Gmail 2FA is enabled
- Verify the app password is correct (not your regular Gmail password)
- Check recipient email addresses are valid

## Support

For issues or questions, refer to:
- Canvas API Documentation: https://canvas.instructure.com/doc/api/
- Gmail App Passwords: https://support.google.com/accounts/answer/185833