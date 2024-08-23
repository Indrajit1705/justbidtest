<%@ page import="com.buysellplatform.model.User" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile</title>
    <link rel="stylesheet" href="styles.css">
    <script type="text/javascript">
        function showPopup(message) {
            alert(message);
        }

        window.onload = function() {
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('passwordChanged') === 'true') {
                showPopup('Your password has been changed successfully.');
                // Remove the query parameter from the URL to prevent the alert from appearing on page refresh
                window.history.replaceState({}, document.title, window.location.pathname);
            }
        };
    </script>
</head>
<body>
    <header>
        <h1>My Profile</h1>
        <nav>
            <a href="index.jsp">Home</a>
            <a href="profile">My Profile</a>
                <a href="myPurchases.jsp">My Purchase Products</a>
                <a href="myListedProducts.jsp">My Listed Products</a>
                <a href="sell.jsp">Sell a Product</a>
                <a href="logout">Logout</a>
            
        </nav>
    </header>
    <main>
        <h2>Personal Information</h2>
        <%
            User user = (User) session.getAttribute("user");
            if (user != null) {
        %>
        <p><strong>Name:</strong> <%= user.getName() %></p>
        <p><strong>Email:</strong> <%= user.getEmail() %></p>
        <p><strong>College:</strong> <%= user.getCollege() %></p>
        <p><strong>WhatsApp Number:</strong> <%= user.getWhatsappNumber() %></p>

        <h2>Change Password</h2>
        <form action="profile" method="post">
            <div>
                <label for="currentPassword">Current Password:</label>
                <input type="password" id="currentPassword" name="currentPassword" required>
            </div>
            <div>
                <label for="newPassword">New Password:</label>
                <input type="password" id="newPassword" name="newPassword" required>
            </div>
            <button type="submit">Update Password</button>
        </form>

        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
            <div class="error-message"><%= errorMessage %></div>
        <% 
            }
        } else { 
        %>
        <p>User information not available.</p>
        <% } %>
    </main>
</body>
</html>
