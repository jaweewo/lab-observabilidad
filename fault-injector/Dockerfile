FROM python:3.12-slim

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY injector.py .

EXPOSE 9999

# Un único worker: el estado de los fallos vive en memoria del proceso
CMD ["gunicorn", "--bind", "0.0.0.0:9999", "--workers", "1", "--threads", "16", "--timeout", "120", "injector:app"]
