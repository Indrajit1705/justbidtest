<%@ page import="java.util.List" %>
<%@ page import="com.buysellplatform.model.Product" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Purchased Products</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <header>
        <h1>My Purchased Products</h1>
        <nav>
            <a href="index.jsp">Home</a>
            <a href="logout">Logout</a>
        </nav>
    </header>
    <main>
        <%
            List<Product> purchasedProducts = (List<Product>) request.getAttribute("purchasedProducts");

            if (purchasedProducts != null && !purchasedProducts.isEmpty()) {
                for (Product product : purchasedProducts) {
        %>
        <div class="product-item">
            <img src="<%= product.getImageUrl() %>" alt="<%= product.getTitle() %>" class="product-image">
            <h3><%= product.getTitle() %></h3>
            <p>Description: <%= product.getDescription() %></p>
            <p>Price: $<%= product.getMinBidPrice() %></p>
        </div>
        <%
                }
            } else {
        %>
        <p>You have not purchased any products yet.</p>
        <%
            }
        %>
    </main>
</body>
</html>
