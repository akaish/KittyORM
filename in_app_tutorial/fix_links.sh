#!/bin/bash
find content -name '*.md' -exec sed -i -e 's/KittyOrmPages/KittyORMPages/g' {} \;
