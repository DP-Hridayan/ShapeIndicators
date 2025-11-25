#!/usr/bin/env python3
import re
import os

VERSION = os.environ.get("LATEST_VERSION")
if not VERSION:
    exit(1)

README_FILE = "README.md"

with open(README_FILE, "r") as f:
    content = f.read()

content = re.sub(
    r'(implementation\s*["\']io\.github\.dp-hridayan:shapeindicators:)[0-9.]+(["\'])',
    fr'\1{VERSION}\2',
    content
)

content = re.sub(
    r'(implementation\(\s*["\']io\.github\.dp-hridayan:shapeindicators:)[0-9.]+(["\']\))',
    fr'\1{VERSION}\2',
    content
)

content = re.sub(
    r'(shapeindicators\s*=\s*")[0-9.]+(")',
    fr'\1{VERSION}\2',
    content
)

with open(README_FILE, "w") as f:
    f.write(content)
