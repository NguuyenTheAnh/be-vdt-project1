# Setup Instructions

## Configuration Setup

1. Copy the template configuration file:
   ```bash
   cp src/main/resources/application.yaml.template src/main/resources/application.yaml
   ```

2. Update the following values in `application.yaml`:
   - `spring.mail.username`: Your Gmail address
   - `spring.mail.password`: Your Gmail app password
   - `spring.datasource.username`: Your PostgreSQL username
   - `spring.datasource.password`: Your PostgreSQL password
   - `jwt.signerKey`: Generate a new UUID for JWT signing

3. The `application.yaml` file is gitignored to protect sensitive information.

## Database Setup

1. Install PostgreSQL
2. Create database: `loan_db`
3. Update credentials in your local `application.yaml`

## Email Setup

1. Enable 2-factor authentication on Gmail
2. Generate an app-specific password
3. Use this app password in the configuration
