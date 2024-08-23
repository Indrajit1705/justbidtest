<%@ page import="java.util.List" %>
<%@ page import="com.buysellplatform.model.Product" %>
<%@ page import="com.buysellplatform.dao.ProductDAO" %>
<%@ page import="com.buysellplatform.model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Listed Products</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <header>
        <h1>My Listed Products</h1>
        <nav>
            <% if (session.getAttribute("user") != null) { %>
                <a href="profile">My Profile</a>
                <a href="myPurchases.jsp">My Purchase Products</a>
                <a href="myListedProducts.jsp">My Listed Products</a>
                <a href="sell.jsp">Sell a Product</a>
                <a href="logout">Logout</a>
            <% } else { %>
                <a href="login.jsp">Login</a>
                <a href="signup.jsp">Signup</a>
            <% } %>
        </nav>
    </header>
    <main>
        <h2>Your Listed Products</h2>
        <%
            // Check if products are already in the request scope
            List<Product> products = (List<Product>) request.getAttribute("products");

            // If not, retrieve them using the ProductDAO
            if (products == null) {
                User user = (User) session.getAttribute("user");

                if (user != null) {
                    ProductDAO productDAO = new ProductDAO();
                    products = productDAO.getProductsBySeller(user.getId());
                    request.setAttribute("products", products);
                } else {
                    response.sendRedirect("login.jsp");
                    return;
                }
            }

            // Display the products or a message if none are listed
            if (products != null && !products.isEmpty()) {
                for (Product product : products) {
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
        <p>You have not listed any products yet.</p>
        <% } %>
    </main>
</body>
</html>
