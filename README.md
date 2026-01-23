# Review Service

Handles user reviews and ratings for fragrances. Users can rate perfumes (1-5) and leave comments. Reviews are public but only the owner can edit/delete them.

## Tech Stack

- Spring Boot 3.x
- Spring Security
- PostgreSQL
- Gradle
- Docker

## Endpoints

### Public
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/reviews/public/{fragranceId}` | Get all reviews for a fragrance |

### User Endpoints (requires auth)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/reviews/me` | Get your reviews |
| POST | `/reviews/{fragranceId}` | Write a review |
| PUT | `/reviews/{fragranceId}/{reviewId}` | Edit your review |
| DELETE | `/reviews/{fragranceId}/{reviewId}` | Delete your review |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| DELETE | `/reviews/admin/{reviewId}` | Delete any review (moderation) |

### Internal (service-to-service)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/internal/reviews/{userId}` | Get user's reviews |
| POST | `/internal/reviews` | Get reviews for multiple fragrances |

## Filtering

Both public and "my reviews" endpoints support filtering:

```
GET /reviews/public/49?ratings=4,5&page=0&size=10&sort=createdAt,desc
GET /reviews/me?fragranceId=49&ratings=4,5
```

## Response Examples

**Public reviews:**
```json
{
  "data": {
    "fragranceId": 49,
    "averageRating": 4.85,
    "reviews": [
      {
        "reviewId": 123,
        "userId": 5,
        "username": "FragranceFan",
        "rating": 5.0,
        "comment": "Amazing scent, lasts all day",
        "createdAt": "2024-01-15T10:30:00"
      }
    ],
    "page": 0,
    "totalElements": 45,
    "totalPages": 5
  }
}
```

**My reviews:**
```json
{
  "data": {
    "reviews": [
      {
        "reviewId": 123,
        "fragranceId": 49,
        "fragranceName": "Baccarat Rouge 540",
        "brand": "Maison Francis Kurkdjian",
        "rating": 5.0,
        "comment": "My signature scent",
        "createdAt": "2024-01-15T10:30:00"
      }
    ]
  }
}
```

## Role in Recommendations

Reviews provide **explicit feedback** for the recommendation engine. The CF algorithm uses reviews with rating >= 4.0 as strong positive signals.

Rating weights:
- 4.8+ = Excellent (1.5x weight)
- 4.5+ = Great (1.25x weight)
- 4.0+ = Good (1.0x weight)

## Environment Variables

```
DB_URL=jdbc:postgresql://localhost:5432/your_db
DB_USERNAME=your_username
DB_PASSWORD=your_password
ACCESS_SECRET=base64_encoded_key
INTERNAL_REVIEW_SECRET=internal_service_key
```

## Running Locally

```bash
./gradlew bootRun
```

Swagger UI: `http://localhost:8084/api/review-service/swagger-ui`

## Docker

```bash
docker build -t review-service .
docker run -p 8084:8084 --env-file .env review-service
```

## Database Schema

```sql
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    fragrance_id BIGINT NOT NULL,
    rating DECIMAL(2,1) NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(user_id, fragrance_id)
);
```

## Project Structure

```
src/main/java/.../review_service/
├── config/          # Security, JWT filter
├── controller/      # Public, User, Admin, Internal endpoints
├── dao/             # Reviews entity
├── dto/             # Request/response objects
├── helper/          # Specification builder, validation
├── services/        # Business logic
└── utilities/       # Token utilities
```

## Related Services

- [Auth Service](../auth-service) - Authentication
- [Fragrance Service](../fragrance-service) - Perfume catalog
- [Collection Service](../collection-service) - User collections
- [Recommendation Service](../recommendation-service) - Uses reviews for CF algorithm
