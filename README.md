# Nexign Bootcamp Task

Microservice for working with CDR (Call Data Record) and UDR (Usage Data Report). The service emulates the operation of a switch, generates and stores CDR records, and also provides an API for obtaining CDR and UDR reports.

## Task Description

Development of a microservice for modeling the process of collecting data about subscriber calls of a mobile operator:

1. Collection of calls in CDR format on switches
2. Aggregation of data into a unified UDR report
3. Providing REST API for working with data

### Main Components:
- **CDR record** - information about a call (type, subscriber numbers, start and end time)
- **UDR report** - aggregated information on subscriber calls

## Technologies

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- Lombok
- JUnit

## API Endpoints

### UDR API

1. **Get UDR by subscriber**
    - **URL**: `/api/udr/subscriber/{msisdn}`
    - **Method**: `GET`
    - **Path parameters**:
        - `msisdn` - subscriber number in format 7XXXXXXXXXX
    - **Query parameters**:
        - `month` (optional) - month (1-12)
    - **Description**: Returns a UDR record for the specified subscriber. If month are specified, returns data for that period, otherwise - for the entire period.

2. **Get UDR records for a month**
    - **URL**: `/api/udr/month/{month}`
    - **Method**: `GET`
    - **Path parameters**:
        - `month` - month (1-12)
    - **Description**: Returns UDR records for all subscribers for the specified month.

### CDR API

1. **Generate CDR report**
    - **URL**: `/api/cdr/generate`
    - **Method**: `POST`
    - **Request body**:
      ```json
      {
        "msisdn": "79001112233",
        "startDate": "2025-01-01T00:00:00",
        "endDate": "2025-01-31T23:59:59"
      }
      ```
    - **Description**: Generate a CDR report for the specified subscriber for the given time period.

## Examples

### Example UDR response

```json
{
  "msisdn": "79001112233",
  "incomingCall": {
    "totalTime": "02:15:30"
  },
  "outcomingCall": {
    "totalTime": "01:45:20"
  }
}
```

### Example response to CDR report generation request

```json
{
  "requestId": "61f0c404-5cb3-11e7-907b-a6006ad3dba0"
}
```

## Author

Konstantin Vasilev - kevasiliev@gmail.com
