<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Bid Confirmation</title>
    <script>
        function showConfirmation() {
            alert("Your bid is placed. The seller will contact you soon after ending the auction.");
        }

        window.onload = function() {
            showConfirmation();
        };
    </script>
</head>
<body>
    <h1>Thank You for Your Bid</h1>
    <p>Your bid has been successfully placed. The seller will contact you soon after ending the auction.</p>
</body>
</html>
