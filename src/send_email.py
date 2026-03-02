#!/usr/bin/env python3
"""
Canvas Assignment Email Notifier

Sends Canvas assignment check results via Gmail.
Reads the assignment check output from stdin and emails it.

Requirements: Python 3.x (built-in libraries only)
"""

import smtplib
import sys
import json
import os
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

def load_config(config_file="config.json"):
    """Load configuration from JSON file."""
    if not os.path.exists(config_file):
        print(f"ERROR: Config file '{config_file}' not found!")
        print(f"Please copy config.example.json to config.json and configure it.")
        sys.exit(1)
    
    with open(config_file, 'r') as f:
        return json.load(f)

def load_secret(file_path):
    """Load secret from file."""
    if not os.path.exists(file_path):
        print(f"ERROR: Secret file '{file_path}' not found!")
        sys.exit(1)
    
    with open(file_path, 'r') as f:
        return f.read().strip()

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
    msg['To'] = ', '.join(to_emails)
    msg['Subject'] = subject
    
    msg.attach(MIMEText(body, 'plain'))
    
    try:
        server = smtplib.SMTP('smtp.gmail.com', 587)
        server.starttls()
        server.login(gmail_user, gmail_app_password)
        text = msg.as_string()
        server.sendmail(gmail_user, to_emails, text)
        server.quit()
        print(f"Email sent successfully to {len(to_emails)} recipient(s)!")
        return True
    except Exception as e:
        print(f"Failed to send email: {e}")
        return False

if __name__ == "__main__":
    # Load configuration
    config = load_config("config.json")
    
    # Load secrets from files
    gmail_user = config['email']['gmailUser']
    gmail_app_password = load_secret(config['email']['appPasswordFilePath'])
    to_emails = config['email']['recipients']
    
    # Read canvas output from stdin
    canvas_output = sys.stdin.read()
    
    # Send the email
    send_email(
        subject="Canvas Assignment Check - Daily Report",
        body=canvas_output,
        to_emails=to_emails,
        gmail_user=gmail_user,
        gmail_app_password=gmail_app_password
    )