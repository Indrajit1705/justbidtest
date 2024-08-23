<%@ page import="java.util.List" %>
<%@ page import="com.buysellplatform.model.Product" %>
<%@ page import="com.buysellplatform.model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home</title>
    <link rel="stylesheet" href="styles.css">
    <script>
        function showAlert(message) {
            alert(message);
            return false;
        }
    </script>
</head>
<body>
    <header>
        <h1>Welcome to JustBid</h1>
        <nav>
            <% if (session.getAttribute("user") != null) { %>
                <a href="profile">My Profile</a>
                <a href="myPurchases.jsp">My Purchase Products</a>
                <a href="myListedProducts.jsp">My Listed Products</a>
                <a href="sell.jsp">Sell a Product</a> <!-- Added "Sell" button -->
                <a href="logout">Logout</a>
            <% } else { %>
                <a href="login.jsp">Login</a>
                <a href="signup.jsp">Signup</a>
            <% } %>
        </nav>
    </header>
    <main>
        <h2>Product Listings</h2>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
        <div class="error-message">
            <p><%= errorMessage %></p>
        </div>
        <%
            }

            List<Product> products = (List<Product>) request.getAttribute("products");
            User currentUser = (User) session.getAttribute("user");
            if (products != null && !products.isEmpty()) {
                for (Product product : products) {
                    boolean isSeller = currentUser != null && product.getSellerId() == currentUser.getId();
        %>
        <div class="product-item">
            <img src="<%= product.getImageUrl() %>" alt="<%= product.getTitle() %>" class="product-image">
            <h3><%= product.getTitle() %></h3>
            <p>Price: $<%= product.getMinBidPrice() %></p>
            <form action="productDetails.jsp" method="get" onsubmit="<%= isSeller ? "return showAlert('This product is listed by you. You cannot buy your own product.');" : "" %>">
                <input type="hidden" name="productId" value="<%= product.getId() %>">
                <% if ("bid".equals(product.getProductType())) { %>
                    <button type="submit" name="action" value="bid">Bid Now</button>
                <% } else if ("sell".equals(product.getProductType())) { %>
                    <button type="submit" name="action" value="buy">Buy Now</button>
                <% } %>
            </form>
        </div>
        <%
                }
            } else {
        %>
        <p>No products available.</p>
        <%
            }
        %>
    </main>
</body>
</html>
