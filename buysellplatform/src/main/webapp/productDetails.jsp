<%@ page import="com.buysellplatform.model.Product, com.buysellplatform.dao.ProductDAO" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="com.buysellplatform.model.User" %>

<%
    // Retrieve the productId and action parameters from the request
    String productIdParam = request.getParameter("productId");
    String actionParam = request.getParameter("action");

    int productId = 0;

    try {
        if (productIdParam != null && !productIdParam.trim().isEmpty()) {
            productId = Integer.parseInt(productIdParam);
        } else {
            out.println("Invalid Product ID");
            return;
        }
    } catch (NumberFormatException e) {
        out.println("Product ID is not a valid integer.");
        return;
    }

    // Create an instance of ProductDAO and retrieve the Product
    ProductDAO productDAO = new ProductDAO();
    Product product = productDAO.getProductById(productId);

    if (product == null) {
        out.println("Product not found");
        return;
    }

    // Retrieve the User object from the session and determine if the user is logged in
    User user = (User) session.getAttribute("user");
    boolean isLoggedIn = (user != null);

    // Get the userId from the User object if logged in
    String userId = isLoggedIn ? String.valueOf(user.getId()) : null;
%>

<html>
<head>
    <title>Product Details</title>
</head>
<body>
    <h1><%= product.getTitle() %></h1>
    <img src="<%= product.getImageUrl() %>" alt="<%= product.getTitle() %>" class="product-image">
    <p>Description: <%= product.getDescription() %></p>
    <p>Minimum Bid Price: $<%= product.getMinBidPrice() %></p>

    <% if (!"buy".equalsIgnoreCase(actionParam)) { %>
        <p>Current Bid Price: $<%= product.getCurrentBidPrice() %></p>
    <% } %>

    <% if ("bid".equalsIgnoreCase(product.getProductType())) { %>
        <form action="BidNowServlet" method="post">
            <input type="hidden" name="productId" value="<%= productId %>">
            <input type="number" name="bidAmount" min="<%= product.getCurrentBidPrice() + 5 %>" step="0.01" required>
            <h4>Bid Price Should be +5$ from current bid</h4>
            <input type="submit" value="Place Bid">
        </form>
    <% } else if ("sell".equalsIgnoreCase(product.getProductType())) { %>
        <form action="BuyNowServlet" method="post">
            <input type="hidden" name="productId" value="<%= productId %>">
            <input type="submit" value="Buy Now">
        </form>
    <% } %>

</body>
</html>
