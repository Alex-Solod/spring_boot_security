#!/bin/bash
# =============================================================
# Comprehensive API Endpoint Test Script
# =============================================================

BASE_URL="http://localhost:8080/api"
PASS=0
FAIL=0
TOTAL=0

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color
BOLD='\033[1m'

UNIQUE_SUFFIX=$(date +%s)

check_response() {
    local test_name="$1"
    local expected_code="$2"
    local actual_code="$3"
    local response_body="$4"
    TOTAL=$((TOTAL + 1))

    if [ "$actual_code" == "$expected_code" ]; then
        PASS=$((PASS + 1))
        echo -e "  ${GREEN}✅ PASS${NC} | ${test_name} | HTTP ${actual_code}"
    else
        FAIL=$((FAIL + 1))
        echo -e "  ${RED}❌ FAIL${NC} | ${test_name} | Expected: ${expected_code}, Got: ${actual_code}"
        if [ -n "$response_body" ]; then
            echo -e "         Response: $(echo "$response_body" | head -c 300)"
        fi
    fi
}

echo -e "\n${BOLD}${CYAN}╔══════════════════════════════════════════════════════╗${NC}"
echo -e "${BOLD}${CYAN}║       Spring Boot Security — API Endpoint Tests      ║${NC}"
echo -e "${BOLD}${CYAN}╚══════════════════════════════════════════════════════╝${NC}\n"

# =============================================================
# 1. AUTH ENDPOINTS
# =============================================================
echo -e "${BOLD}${YELLOW}━━━ 1. Authentication Endpoints (/auth) ━━━${NC}\n"

# 1.1 Register new USER
echo -e "${CYAN}  ▸ Registering new USER...${NC}"
REGISTER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"testuser_${UNIQUE_SUFFIX}@example.com\",
    \"password\": \"password123\",
    \"repeatPassword\": \"password123\",
    \"firstName\": \"Test\",
    \"lastName\": \"User\",
    \"shippingAddress\": \"123 Test St\"
  }")
REGISTER_CODE=$(echo "$REGISTER_RESPONSE" | tail -1)
REGISTER_BODY=$(echo "$REGISTER_RESPONSE" | sed '$d')
check_response "POST /auth/register (new USER)" "200" "$REGISTER_CODE" "$REGISTER_BODY"
echo -e "         Body: $REGISTER_BODY"

# 1.2 Register duplicate user (should fail)
echo -e "${CYAN}  ▸ Registering duplicate user...${NC}"
DUP_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"testuser_${UNIQUE_SUFFIX}@example.com\",
    \"password\": \"password123\",
    \"repeatPassword\": \"password123\",
    \"firstName\": \"Test\",
    \"lastName\": \"User\",
    \"shippingAddress\": \"123 Test St\"
  }")
DUP_CODE=$(echo "$DUP_RESPONSE" | tail -1)
DUP_BODY=$(echo "$DUP_RESPONSE" | sed '$d')
# Expect 409 Conflict or 400 Bad Request
if [ "$DUP_CODE" -ge 400 ] && [ "$DUP_CODE" -lt 500 ]; then
    TOTAL=$((TOTAL + 1))
    PASS=$((PASS + 1))
    echo -e "  ${GREEN}✅ PASS${NC} | POST /auth/register (duplicate) | HTTP ${DUP_CODE} (expected 4xx)"
else
    TOTAL=$((TOTAL + 1))
    FAIL=$((FAIL + 1))
    echo -e "  ${RED}❌ FAIL${NC} | POST /auth/register (duplicate) | Expected 4xx, Got: ${DUP_CODE}"
fi

# 1.3 Register with invalid data (validation)
echo -e "${CYAN}  ▸ Registering with invalid data...${NC}"
INVALID_REG=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"not-an-email\",
    \"password\": \"short\",
    \"repeatPassword\": \"different\",
    \"firstName\": \"\",
    \"lastName\": \"\"
  }")
INVALID_CODE=$(echo "$INVALID_REG" | tail -1)
INVALID_BODY=$(echo "$INVALID_REG" | sed '$d')
if [ "$INVALID_CODE" -ge 400 ] && [ "$INVALID_CODE" -lt 500 ]; then
    TOTAL=$((TOTAL + 1))
    PASS=$((PASS + 1))
    echo -e "  ${GREEN}✅ PASS${NC} | POST /auth/register (invalid data) | HTTP ${INVALID_CODE} (expected 4xx)"
else
    TOTAL=$((TOTAL + 1))
    FAIL=$((FAIL + 1))
    echo -e "  ${RED}❌ FAIL${NC} | POST /auth/register (invalid data) | Expected 4xx, Got: ${INVALID_CODE}"
fi

# 1.4 Login as USER
echo -e "${CYAN}  ▸ Logging in as USER...${NC}"
LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"testuser_${UNIQUE_SUFFIX}@example.com\",
    \"password\": \"password123\"
  }")
LOGIN_CODE=$(echo "$LOGIN_RESPONSE" | tail -1)
LOGIN_BODY=$(echo "$LOGIN_RESPONSE" | sed '$d')
check_response "POST /auth/login (valid credentials)" "200" "$LOGIN_CODE" "$LOGIN_BODY"
USER_TOKEN=$(echo "$LOGIN_BODY" | python3 -c "import sys,json; print(json.load(sys.stdin).get('token',''))" 2>/dev/null)
if [ -n "$USER_TOKEN" ]; then
    echo -e "         ${GREEN}Token obtained ✓${NC} (${USER_TOKEN:0:30}...)"
else
    echo -e "         ${RED}No token in response!${NC}"
    echo -e "         Body: $LOGIN_BODY"
fi

# 1.5 Login with wrong password
echo -e "${CYAN}  ▸ Logging in with wrong password...${NC}"
BAD_LOGIN=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"testuser_${UNIQUE_SUFFIX}@example.com\",
    \"password\": \"wrongpassword\"
  }")
BAD_CODE=$(echo "$BAD_LOGIN" | tail -1)
if [ "$BAD_CODE" -ge 400 ]; then
    TOTAL=$((TOTAL + 1))
    PASS=$((PASS + 1))
    echo -e "  ${GREEN}✅ PASS${NC} | POST /auth/login (wrong password) | HTTP ${BAD_CODE} (expected 4xx)"
else
    TOTAL=$((TOTAL + 1))
    FAIL=$((FAIL + 1))
    echo -e "  ${RED}❌ FAIL${NC} | POST /auth/login (wrong password) | Expected 4xx, Got: ${BAD_CODE}"
fi

# Register and login as ADMIN — check if there's a default admin
# Try to login as admin@example.com or create one via DB
echo -e "${CYAN}  ▸ Registering ADMIN user...${NC}"
ADMIN_REG=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"admin_${UNIQUE_SUFFIX}@example.com\",
    \"password\": \"adminpass123\",
    \"repeatPassword\": \"adminpass123\",
    \"firstName\": \"Admin\",
    \"lastName\": \"Test\",
    \"shippingAddress\": \"Admin St 1\"
  }")
ADMIN_REG_CODE=$(echo "$ADMIN_REG" | tail -1)
echo -e "         Admin register: HTTP $ADMIN_REG_CODE"

# Grant ADMIN role via DB
echo -e "${CYAN}  ▸ Granting ADMIN role via DB...${NC}"
ADMIN_EMAIL="admin_${UNIQUE_SUFFIX}@example.com"
mysql -h 127.0.0.1 -P 3307 -u root -pyour_password book_db -e "
  INSERT IGNORE INTO users_roles (user_id, role_id)
  SELECT u.id, r.id FROM users u, roles r
  WHERE u.email = '${ADMIN_EMAIL}' AND r.name = 'ADMIN';
" 2>/dev/null
if [ $? -eq 0 ]; then
    echo -e "         ${GREEN}ADMIN role granted ✓${NC}"
else
    echo -e "         ${YELLOW}Could not grant ADMIN role via mysql CLI — trying alternative${NC}"
    # Try docker exec
    docker exec spring_boot_security-mysqldb-1 mysql -u root -pyour_password book_db -e "
      INSERT IGNORE INTO users_roles (user_id, role_id)
      SELECT u.id, r.id FROM users u, roles r
      WHERE u.email = '${ADMIN_EMAIL}' AND r.name = 'ADMIN';
    " 2>/dev/null
fi

# Login as ADMIN
echo -e "${CYAN}  ▸ Logging in as ADMIN...${NC}"
ADMIN_LOGIN=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"${ADMIN_EMAIL}\",
    \"password\": \"adminpass123\"
  }")
ADMIN_LOGIN_CODE=$(echo "$ADMIN_LOGIN" | tail -1)
ADMIN_LOGIN_BODY=$(echo "$ADMIN_LOGIN" | sed '$d')
ADMIN_TOKEN=$(echo "$ADMIN_LOGIN_BODY" | python3 -c "import sys,json; print(json.load(sys.stdin).get('token',''))" 2>/dev/null)
if [ -n "$ADMIN_TOKEN" ]; then
    echo -e "         ${GREEN}ADMIN Token obtained ✓${NC} (${ADMIN_TOKEN:0:30}...)"
else
    echo -e "         ${RED}No ADMIN token!${NC} Body: $ADMIN_LOGIN_BODY"
fi

echo ""

# =============================================================
# 2. CATEGORY ENDPOINTS (ADMIN)
# =============================================================
echo -e "${BOLD}${YELLOW}━━━ 2. Category Endpoints (/categories) ━━━${NC}\n"

# 2.1 Create category (ADMIN)
echo -e "${CYAN}  ▸ Creating category (ADMIN)...${NC}"
CAT_CREATE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/categories" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d "{
    \"name\": \"Fiction_${UNIQUE_SUFFIX}\",
    \"description\": \"Fiction books\"
  }")
CAT_CREATE_CODE=$(echo "$CAT_CREATE" | tail -1)
CAT_CREATE_BODY=$(echo "$CAT_CREATE" | sed '$d')
check_response "POST /categories (ADMIN create)" "201" "$CAT_CREATE_CODE" "$CAT_CREATE_BODY"
CATEGORY_ID=$(echo "$CAT_CREATE_BODY" | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null)
echo -e "         Category ID: $CATEGORY_ID"

# 2.2 Create category (USER — should fail 403)
echo -e "${CYAN}  ▸ Creating category (USER — should fail)...${NC}"
CAT_USER=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/categories" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -d "{\"name\": \"ShouldFail\", \"description\": \"test\"}")
CAT_USER_CODE=$(echo "$CAT_USER" | tail -1)
check_response "POST /categories (USER — 403)" "403" "$CAT_USER_CODE"

# 2.3 Get all categories (USER)
echo -e "${CYAN}  ▸ Getting all categories (USER)...${NC}"
CAT_ALL=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/categories" \
  -H "Authorization: Bearer $USER_TOKEN")
CAT_ALL_CODE=$(echo "$CAT_ALL" | tail -1)
CAT_ALL_BODY=$(echo "$CAT_ALL" | sed '$d')
check_response "GET /categories (USER)" "200" "$CAT_ALL_CODE" "$CAT_ALL_BODY"

# 2.4 Get category by ID (USER)
echo -e "${CYAN}  ▸ Getting category by ID (USER)...${NC}"
if [ -n "$CATEGORY_ID" ]; then
    CAT_BY_ID=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/categories/$CATEGORY_ID" \
      -H "Authorization: Bearer $USER_TOKEN")
    CAT_BY_ID_CODE=$(echo "$CAT_BY_ID" | tail -1)
    CAT_BY_ID_BODY=$(echo "$CAT_BY_ID" | sed '$d')
    check_response "GET /categories/{id} (USER)" "200" "$CAT_BY_ID_CODE" "$CAT_BY_ID_BODY"
else
    echo -e "  ${YELLOW}⚠️  SKIP${NC} | No category ID available"
fi

# 2.5 Update category (ADMIN)
echo -e "${CYAN}  ▸ Updating category (ADMIN)...${NC}"
if [ -n "$CATEGORY_ID" ]; then
    CAT_UPDATE=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/categories/$CATEGORY_ID" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -d "{\"name\": \"Updated Fiction\", \"description\": \"Updated description\"}")
    CAT_UPDATE_CODE=$(echo "$CAT_UPDATE" | tail -1)
    check_response "PUT /categories/{id} (ADMIN update)" "200" "$CAT_UPDATE_CODE"
fi

# 2.6 Get non-existent category (should 404)
echo -e "${CYAN}  ▸ Getting non-existent category...${NC}"
CAT_404=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/categories/99999" \
  -H "Authorization: Bearer $USER_TOKEN")
CAT_404_CODE=$(echo "$CAT_404" | tail -1)
if [ "$CAT_404_CODE" -ge 400 ]; then
    TOTAL=$((TOTAL + 1))
    PASS=$((PASS + 1))
    echo -e "  ${GREEN}✅ PASS${NC} | GET /categories/99999 (not found) | HTTP ${CAT_404_CODE}"
else
    TOTAL=$((TOTAL + 1))
    FAIL=$((FAIL + 1))
    echo -e "  ${RED}❌ FAIL${NC} | GET /categories/99999 (not found) | Expected 4xx, Got: ${CAT_404_CODE}"
fi

# 2.7 Get without auth (should 401/403)
echo -e "${CYAN}  ▸ Getting categories without auth...${NC}"
CAT_NOAUTH=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/categories")
CAT_NOAUTH_CODE=$(echo "$CAT_NOAUTH" | tail -1)
if [ "$CAT_NOAUTH_CODE" -ge 400 ]; then
    TOTAL=$((TOTAL + 1))
    PASS=$((PASS + 1))
    echo -e "  ${GREEN}✅ PASS${NC} | GET /categories (no auth) | HTTP ${CAT_NOAUTH_CODE}"
else
    TOTAL=$((TOTAL + 1))
    FAIL=$((FAIL + 1))
    echo -e "  ${RED}❌ FAIL${NC} | GET /categories (no auth) | Expected 4xx, Got: ${CAT_NOAUTH_CODE}"
fi

echo ""

# =============================================================
# 3. BOOK ENDPOINTS
# =============================================================
echo -e "${BOLD}${YELLOW}━━━ 3. Book Endpoints (/books) ━━━${NC}\n"

# 3.1 Create book (ADMIN)
echo -e "${CYAN}  ▸ Creating book (ADMIN)...${NC}"
CATEGORY_IDS_JSON="[]"
if [ -n "$CATEGORY_ID" ]; then
    CATEGORY_IDS_JSON="[$CATEGORY_ID]"
fi
BOOK_CREATE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/books" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d "{
    \"title\": \"Test Book ${UNIQUE_SUFFIX}\",
    \"author\": \"Test Author\",
    \"isbn\": \"978-0-13-${UNIQUE_SUFFIX}\",
    \"price\": 29.99,
    \"description\": \"A great test book\",
    \"coverImage\": \"https://example.com/cover.jpg\",
    \"categoryIds\": $CATEGORY_IDS_JSON
  }")
BOOK_CREATE_CODE=$(echo "$BOOK_CREATE" | tail -1)
BOOK_CREATE_BODY=$(echo "$BOOK_CREATE" | sed '$d')
check_response "POST /books (ADMIN create)" "201" "$BOOK_CREATE_CODE" "$BOOK_CREATE_BODY"
BOOK_ID=$(echo "$BOOK_CREATE_BODY" | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null)
echo -e "         Book ID: $BOOK_ID"

# 3.2 Create book (USER — should fail 403)
echo -e "${CYAN}  ▸ Creating book (USER — should fail)...${NC}"
BOOK_USER=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/books" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -d "{\"title\": \"Fail\", \"author\": \"Fail\", \"isbn\": \"000\", \"price\": 1.0}")
BOOK_USER_CODE=$(echo "$BOOK_USER" | tail -1)
check_response "POST /books (USER — 403)" "403" "$BOOK_USER_CODE"

# 3.3 Get all books (USER)
echo -e "${CYAN}  ▸ Getting all books (USER)...${NC}"
BOOKS_ALL=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/books" \
  -H "Authorization: Bearer $USER_TOKEN")
BOOKS_ALL_CODE=$(echo "$BOOKS_ALL" | tail -1)
BOOKS_ALL_BODY=$(echo "$BOOKS_ALL" | sed '$d')
check_response "GET /books (USER)" "200" "$BOOKS_ALL_CODE"

# 3.4 Get book by ID (USER)
echo -e "${CYAN}  ▸ Getting book by ID (USER)...${NC}"
if [ -n "$BOOK_ID" ]; then
    BOOK_BY_ID=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/books/$BOOK_ID" \
      -H "Authorization: Bearer $USER_TOKEN")
    BOOK_BY_ID_CODE=$(echo "$BOOK_BY_ID" | tail -1)
    BOOK_BY_ID_BODY=$(echo "$BOOK_BY_ID" | sed '$d')
    check_response "GET /books/{id} (USER)" "200" "$BOOK_BY_ID_CODE" "$BOOK_BY_ID_BODY"
    echo -e "         Body: $BOOK_BY_ID_BODY"
fi

# 3.5 Update book (ADMIN)
echo -e "${CYAN}  ▸ Updating book (ADMIN)...${NC}"
if [ -n "$BOOK_ID" ]; then
    BOOK_UPDATE=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/books/$BOOK_ID" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -d "{
        \"title\": \"Updated Book\",
        \"author\": \"Updated Author\",
        \"isbn\": \"978-0-13-${UNIQUE_SUFFIX}\",
        \"price\": 39.99,
        \"description\": \"Updated description\",
        \"coverImage\": \"https://example.com/updated.jpg\",
        \"categoryIds\": $CATEGORY_IDS_JSON
      }")
    BOOK_UPDATE_CODE=$(echo "$BOOK_UPDATE" | tail -1)
    check_response "PUT /books/{id} (ADMIN update)" "200" "$BOOK_UPDATE_CODE"
fi

# 3.6 Get books by category ID
echo -e "${CYAN}  ▸ Getting books by category ID...${NC}"
if [ -n "$CATEGORY_ID" ]; then
    BOOKS_CAT=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/categories/$CATEGORY_ID/books" \
      -H "Authorization: Bearer $USER_TOKEN")
    BOOKS_CAT_CODE=$(echo "$BOOKS_CAT" | tail -1)
    check_response "GET /categories/{id}/books (USER)" "200" "$BOOKS_CAT_CODE"
fi

# 3.7 Get book without auth
echo -e "${CYAN}  ▸ Getting books without auth...${NC}"
BOOKS_NOAUTH=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/books")
BOOKS_NOAUTH_CODE=$(echo "$BOOKS_NOAUTH" | tail -1)
if [ "$BOOKS_NOAUTH_CODE" -ge 400 ]; then
    TOTAL=$((TOTAL + 1))
    PASS=$((PASS + 1))
    echo -e "  ${GREEN}✅ PASS${NC} | GET /books (no auth) | HTTP ${BOOKS_NOAUTH_CODE}"
else
    TOTAL=$((TOTAL + 1))
    FAIL=$((FAIL + 1))
    echo -e "  ${RED}❌ FAIL${NC} | GET /books (no auth) | Expected 4xx, Got: ${BOOKS_NOAUTH_CODE}"
fi

echo ""

# =============================================================
# 4. SHOPPING CART ENDPOINTS
# =============================================================
echo -e "${BOLD}${YELLOW}━━━ 4. Shopping Cart Endpoints (/cart) ━━━${NC}\n"

# 4.1 Add book to cart (USER)
echo -e "${CYAN}  ▸ Adding book to cart (USER)...${NC}"
if [ -n "$BOOK_ID" ]; then
    CART_ADD=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/cart" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $USER_TOKEN" \
      -d "{\"bookId\": $BOOK_ID, \"quantity\": 2}")
    CART_ADD_CODE=$(echo "$CART_ADD" | tail -1)
    CART_ADD_BODY=$(echo "$CART_ADD" | sed '$d')
    check_response "POST /cart (USER add book)" "200" "$CART_ADD_CODE" "$CART_ADD_BODY"
    echo -e "         Body: $(echo "$CART_ADD_BODY" | head -c 200)"
    
    # Extract cart item ID
    CART_ITEM_ID=$(echo "$CART_ADD_BODY" | python3 -c "
import sys, json
data = json.load(sys.stdin)
items = data.get('cartItems', [])
if items:
    print(items[0].get('id', ''))
" 2>/dev/null)
    echo -e "         Cart Item ID: $CART_ITEM_ID"
fi

# 4.2 Get shopping cart (USER)
echo -e "${CYAN}  ▸ Getting shopping cart (USER)...${NC}"
CART_GET=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/cart" \
  -H "Authorization: Bearer $USER_TOKEN")
CART_GET_CODE=$(echo "$CART_GET" | tail -1)
CART_GET_BODY=$(echo "$CART_GET" | sed '$d')
check_response "GET /cart (USER)" "200" "$CART_GET_CODE" "$CART_GET_BODY"

# 4.3 Update cart item quantity (USER)
echo -e "${CYAN}  ▸ Updating cart item quantity (USER)...${NC}"
if [ -n "$CART_ITEM_ID" ] && [ -n "$BOOK_ID" ]; then
    CART_UPDATE=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/cart/items/$CART_ITEM_ID" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $USER_TOKEN" \
      -d "{\"bookId\": $BOOK_ID, \"quantity\": 5}")
     CART_UPDATE_CODE=$(echo "$CART_UPDATE" | tail -1)
    check_response "PUT /cart/items/{id} (USER update qty)" "200" "$CART_UPDATE_CODE"
else
    echo -e "  ${YELLOW}⚠️  SKIP${NC} | No cart item ID available"
fi

# 4.3b Delete temporary cart item (USER)
echo -e "${CYAN}  ▸ Deleting temporary cart item (USER)...${NC}"
if [ -n "$BOOK_ID" ]; then
    TEMP_ADD=$(curl -s -X POST "$BASE_URL/cart" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $USER_TOKEN" \
      -d "{\"bookId\": $BOOK_ID, \"quantity\": 1}")
    TEMP_ITEM_ID=$(echo "$TEMP_ADD" | python3 -c "
import sys, json
data = json.load(sys.stdin)
items = data.get('cartItems', [])
if items:
    for item in items:
        if item.get('quantity') == 1:
            print(item.get('id', ''))
            break
" 2>/dev/null)
    if [ -n "$TEMP_ITEM_ID" ]; then
        CART_DEL_TEMP=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/cart/items/$TEMP_ITEM_ID" \
          -H "Authorization: Bearer $USER_TOKEN")
        CART_DEL_TEMP_CODE=$(echo "$CART_DEL_TEMP" | tail -1)
        check_response "DELETE /cart/items/{id} (USER)" "204" "$CART_DEL_TEMP_CODE"
    else
        echo -e "  ${YELLOW}⚠️  SKIP${NC} | Could not add/find temporary cart item"
    fi
else
    echo -e "  ${YELLOW}⚠️  SKIP${NC} | No book ID available"
fi

# 4.4 Get cart without auth
echo -e "${CYAN}  ▸ Getting cart without auth...${NC}"
CART_NOAUTH=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/cart")
CART_NOAUTH_CODE=$(echo "$CART_NOAUTH" | tail -1)
if [ "$CART_NOAUTH_CODE" -ge 400 ]; then
    TOTAL=$((TOTAL + 1))
    PASS=$((PASS + 1))
    echo -e "  ${GREEN}✅ PASS${NC} | GET /cart (no auth) | HTTP ${CART_NOAUTH_CODE}"
else
    TOTAL=$((TOTAL + 1))
    FAIL=$((FAIL + 1))
    echo -e "  ${RED}❌ FAIL${NC} | GET /cart (no auth) | Expected 4xx, Got: ${CART_NOAUTH_CODE}"
fi

echo ""

# =============================================================
# 5. ORDER ENDPOINTS
# =============================================================
echo -e "${BOLD}${YELLOW}━━━ 5. Order Endpoints (/orders) ━━━${NC}\n"

# 5.1 Place order (USER)
echo -e "${CYAN}  ▸ Placing order (USER)...${NC}"
ORDER_CREATE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/orders" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -d "{\"shippingAddress\": \"123 Delivery St, Test City\"}")
ORDER_CREATE_CODE=$(echo "$ORDER_CREATE" | tail -1)
ORDER_CREATE_BODY=$(echo "$ORDER_CREATE" | sed '$d')
check_response "POST /orders (USER place order)" "201" "$ORDER_CREATE_CODE" "$ORDER_CREATE_BODY"
ORDER_ID=$(echo "$ORDER_CREATE_BODY" | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null)
echo -e "         Order ID: $ORDER_ID"
echo -e "         Body: $(echo "$ORDER_CREATE_BODY" | head -c 300)"

# 5.2 Get order history (USER)
echo -e "${CYAN}  ▸ Getting order history (USER)...${NC}"
ORDER_HISTORY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/orders" \
  -H "Authorization: Bearer $USER_TOKEN")
ORDER_HISTORY_CODE=$(echo "$ORDER_HISTORY" | tail -1)
ORDER_HISTORY_BODY=$(echo "$ORDER_HISTORY" | sed '$d')
check_response "GET /orders (USER history)" "200" "$ORDER_HISTORY_CODE"

# 5.3 Update order status (ADMIN)
echo -e "${CYAN}  ▸ Updating order status (ADMIN)...${NC}"
if [ -n "$ORDER_ID" ]; then
    ORDER_STATUS=$(curl -s -w "\n%{http_code}" -X PATCH "$BASE_URL/orders/$ORDER_ID" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -d "{\"status\": \"COMPLETED\"}")
    ORDER_STATUS_CODE=$(echo "$ORDER_STATUS" | tail -1)
    check_response "PATCH /orders/{id} (ADMIN update status)" "200" "$ORDER_STATUS_CODE"
fi

# 5.4 Update order status (USER — should fail 403)
echo -e "${CYAN}  ▸ Updating order status (USER — should fail)...${NC}"
if [ -n "$ORDER_ID" ]; then
    ORDER_STATUS_USER=$(curl -s -w "\n%{http_code}" -X PATCH "$BASE_URL/orders/$ORDER_ID" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $USER_TOKEN" \
      -d "{\"status\": \"DELIVERED\"}")
    ORDER_STATUS_USER_CODE=$(echo "$ORDER_STATUS_USER" | tail -1)
    check_response "PATCH /orders/{id} (USER — 403)" "403" "$ORDER_STATUS_USER_CODE"
fi

# 5.5 Get order items (USER)
echo -e "${CYAN}  ▸ Getting order items (USER)...${NC}"
if [ -n "$ORDER_ID" ]; then
    ORDER_ITEMS=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/orders/$ORDER_ID/item" \
      -H "Authorization: Bearer $USER_TOKEN")
    ORDER_ITEMS_CODE=$(echo "$ORDER_ITEMS" | tail -1)
    ORDER_ITEMS_BODY=$(echo "$ORDER_ITEMS" | sed '$d')
    check_response "GET /orders/{id}/item (USER)" "200" "$ORDER_ITEMS_CODE" "$ORDER_ITEMS_BODY"
fi

# 5.6 Orders without auth
echo -e "${CYAN}  ▸ Getting orders without auth...${NC}"
ORDERS_NOAUTH=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/orders")
ORDERS_NOAUTH_CODE=$(echo "$ORDERS_NOAUTH" | tail -1)
if [ "$ORDERS_NOAUTH_CODE" -ge 400 ]; then
    TOTAL=$((TOTAL + 1))
    PASS=$((PASS + 1))
    echo -e "  ${GREEN}✅ PASS${NC} | GET /orders (no auth) | HTTP ${ORDERS_NOAUTH_CODE}"
else
    TOTAL=$((TOTAL + 1))
    FAIL=$((FAIL + 1))
    echo -e "  ${RED}❌ FAIL${NC} | GET /orders (no auth) | Expected 4xx, Got: ${ORDERS_NOAUTH_CODE}"
fi

echo ""

# =============================================================
# 6. DELETE / CLEANUP TESTS
# =============================================================
echo -e "${BOLD}${YELLOW}━━━ 6. Delete Endpoints ━━━${NC}\n"

# 6.1 Delete cart item (USER - expect 404 since order placed has cleared it)
echo -e "${CYAN}  ▸ Deleting cart item (USER - expect 404)...${NC}"
if [ -n "$CART_ITEM_ID" ]; then
    CART_DEL=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/cart/items/$CART_ITEM_ID" \
      -H "Authorization: Bearer $USER_TOKEN")
    CART_DEL_CODE=$(echo "$CART_DEL" | tail -1)
    check_response "DELETE /cart/items/{id} (USER - 404)" "404" "$CART_DEL_CODE"
else
    echo -e "  ${YELLOW}⚠️  SKIP${NC} | No cart item to delete"
fi

# 6.2 Delete book (ADMIN)
echo -e "${CYAN}  ▸ Deleting book (ADMIN)...${NC}"
if [ -n "$BOOK_ID" ]; then
    BOOK_DEL=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/books/$BOOK_ID" \
      -H "Authorization: Bearer $ADMIN_TOKEN")
    BOOK_DEL_CODE=$(echo "$BOOK_DEL" | tail -1)
    check_response "DELETE /books/{id} (ADMIN)" "204" "$BOOK_DEL_CODE"
fi

# 6.3 Delete book (USER — should fail 403)
echo -e "${CYAN}  ▸ Deleting book (USER — should fail)...${NC}"
if [ -n "$BOOK_ID" ]; then
    BOOK_DEL_USER=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/books/$BOOK_ID" \
      -H "Authorization: Bearer $USER_TOKEN")
    BOOK_DEL_USER_CODE=$(echo "$BOOK_DEL_USER" | tail -1)
    check_response "DELETE /books/{id} (USER — 403)" "403" "$BOOK_DEL_USER_CODE"
fi

# 6.4 Delete category (ADMIN)
echo -e "${CYAN}  ▸ Deleting category (ADMIN)...${NC}"
if [ -n "$CATEGORY_ID" ]; then
    CAT_DEL=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/categories/$CATEGORY_ID" \
      -H "Authorization: Bearer $ADMIN_TOKEN")
    CAT_DEL_CODE=$(echo "$CAT_DEL" | tail -1)
    check_response "DELETE /categories/{id} (ADMIN)" "204" "$CAT_DEL_CODE"
fi

# 6.5 Delete category (USER — should fail 403)
echo -e "${CYAN}  ▸ Deleting category (USER — should fail)...${NC}"
if [ -n "$CATEGORY_ID" ]; then
    CAT_DEL_USER=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/categories/$CATEGORY_ID" \
      -H "Authorization: Bearer $USER_TOKEN")
    CAT_DEL_USER_CODE=$(echo "$CAT_DEL_USER" | tail -1)
    check_response "DELETE /categories/{id} (USER — 403)" "403" "$CAT_DEL_USER_CODE"
fi

echo ""

# =============================================================
# 7. SWAGGER / API DOCS
# =============================================================
echo -e "${BOLD}${YELLOW}━━━ 7. Swagger / API Docs ━━━${NC}\n"

echo -e "${CYAN}  ▸ Checking Swagger UI...${NC}"
SWAGGER=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/swagger-ui/index.html")
SWAGGER_CODE=$(echo "$SWAGGER" | tail -1)
check_response "GET /swagger-ui/index.html (public)" "200" "$SWAGGER_CODE"

echo -e "${CYAN}  ▸ Checking API docs...${NC}"
APIDOCS=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/v3/api-docs")
APIDOCS_CODE=$(echo "$APIDOCS" | tail -1)
check_response "GET /v3/api-docs (public)" "200" "$APIDOCS_CODE"

echo ""

# =============================================================
# SUMMARY
# =============================================================
echo -e "${BOLD}${CYAN}╔══════════════════════════════════════════════════════╗${NC}"
echo -e "${BOLD}${CYAN}║                    TEST SUMMARY                      ║${NC}"
echo -e "${BOLD}${CYAN}╚══════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "  Total Tests:  ${BOLD}${TOTAL}${NC}"
echo -e "  ${GREEN}Passed:       ${PASS}${NC}"
echo -e "  ${RED}Failed:       ${FAIL}${NC}"
echo ""

if [ $FAIL -eq 0 ]; then
    echo -e "  ${GREEN}${BOLD}🎉 ALL TESTS PASSED!${NC}"
else
    echo -e "  ${RED}${BOLD}⚠️  ${FAIL} TEST(S) FAILED${NC}"
fi
echo ""
