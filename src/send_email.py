#!/usr/bin/env python3
"""
Canvas Assignment Email Notifier

Sends Canvas assignment check results via Gmail.
Reads the assignment check output from stdin and emails it.

Requirements: Python 3.x (built-in libraries only)
"""

import smtplib
import sys
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

def send_email(subject, body, to_emails, gmail_user, gmail_app_password):
    """
    Send email using Gmail SMTP server.
    
    Args:
        subject: Email subject line
        body: Email body content
        to_emails: List of recipient email addresses (or single email string)
        gmail_user: Sender Gmail address
        gmail_app_password: Gmail App Password (NOT regular password)
    
    Returns:
        True if email sent successfully, False otherwise
    """
    
    # Convert single email to list for consistency
    if isinstance(to_emails, str):
        to_emails = [to_emails]
    
    msg = MIMEMultipart()
    msg['From'] = gmail_user
    msg['To'] = ', '.join(to_emails)  # Join multiple emails with commas
    msg['Subject'] = subject
    
    msg.attach(MIMEText(body, 'plain'))
    
    try:
        server = smtplib.SMTP('smtp.gmail.com', 587)
        server.starttls()
        server.login(gmail_user, gmail_app_password)
        text = msg.as_string()
        server.sendmail(gmail_user, to_emails, text)  # Send to all recipients
        server.quit()
        print(f"Email sent successfully to {len(to_emails)} recipient(s)!")
        return True
    except Exception as e:
        print(f"Failed to send email: {e}")
        return False

if __name__ == "__main__":
    # Read canvas output from stdin
    canvas_output = sys.stdin.read()
    
    # Gmail credentials - CONFIGURE THESE BEFORE USE
    # Generate an App Password at: https://myaccount.google.com/apppasswords
    GMAIL_USER = "vihaan.m.shah@gmail.com"  # Your Gmail address
    GMAIL_APP_PASSWORD = "uassgqregnzzaldy"  # 16-character Gmail App Password
    
    # Recipients - can be a single email or a list of multiple emails
    TO_EMAILS = [
        "vihaan.m.shah@gmail.com",      # Your email
        "maushah@gmail.com",        # Additional recipient 1
        "rachshah20@gmail.com"         # Additional recipient 2
    ]
    # Or for a single recipient, use: TO_EMAILS = "your-email@gmail.com"
    
    # Validate configuration
    if GMAIL_USER != "vihaan.m.shah@gmail.com" or GMAIL_APP_PASSWORD != "uassgqregnzzaldy":
        print("ERROR: Please configure your Gmail credentials in send_email.py")
        print("See README.md for setup instructions")
        sys.exit(1)
    
    # Send the email
    send_email(
        subject="Canvas Assignment Check - Daily Report",
        body=canvas_output,
        to_emails=TO_EMAILS,
        gmail_user=GMAIL_USER,
        gmail_app_password=GMAIL_APP_PASSWORD
    )

