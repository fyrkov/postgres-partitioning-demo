# Creating an image of Postgres with the partman extension
from postgres:17

run apt-get update \
  && apt-get install -y --no-install-recommends postgresql-17-partman \
  && rm -rf /var/lib/apt/lists/*