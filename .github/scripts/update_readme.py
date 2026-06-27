#!/usr/bin/env python3
import re
import os

VERSION = os.environ.get("LATEST_VERSION")
if not VERSION:
    print("ERROR: LATEST_VERSION is empty, aborting.")
    exit(1)

print(f"Updating README to version: {VERSION}")

README_FILE = "README.md"

with open(README_FILE, "r") as f:
    content = f.read()

# Groovy style: implementation "io.github.dp-hridayan:shapeindicators:x.y.z"
pattern1 = r"""(implementation\s*["']io\.github\.dp-hridayan:shapeindicators:)[0-9.]+(["'])"""

# Kotlin style: implementation("io.github.dp-hridayan:shapeindicators:x.y.z")
pattern2 = r"""(implementation\(\s*["']io\.github\.dp-hridayan:shapeindicators:)[0-9.]+(["']\))"""

# TOML style: shapeindicators = "x.y.z"
pattern3 = r'(shapeindicators\s*=\s*")[0-9.]+(")' 

content = re.sub(pattern1, lambda m: f"{m.group(1)}{VERSION}{m.group(2)}", content)
content = re.sub(pattern2, lambda m: f"{m.group(1)}{VERSION}{m.group(2)}", content)
content = re.sub(pattern3, lambda m: f"{m.group(1)}{VERSION}{m.group(2)}", content)

with open(README_FILE, "w") as f:
    f.write(content)

print("Done.")
