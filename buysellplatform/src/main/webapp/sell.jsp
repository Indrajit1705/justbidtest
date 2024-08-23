<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Sell a Product</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>
<body>
    <div class="container">
        <h2>List a New Product</h2>
        <form action="sell" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label for="title">Product Title:</label>
                <input type="text" name="title" id="title" required>
            </div>
            <div class="form-group">
                <label for="image">Upload Image:</label>
                <h6>Only JPG, JPEG, PNG, PDF, and HEIC files are accepted.</h6>
                <input type="file" name="image" id="image" required>
            </div>
            <div class="form-group">
                <label for="description">Product Description:</label>
                <textarea name="description" id="description" required></textarea>
            </div>
            <div class="form-group">
                <label for="minBidPrice">Minimum Bid Price:</label>
                <input type="number" name="minBidPrice" id="minBidPrice" step="0.01" required>
            </div>
            <div class="form-group">
                <label for="auctionEndDate">Auction End Date:</label>
                <input type="date" name="auctionEndDate" id="auctionEndDate" required>
            </div>
            <div class="form-group">
                <label for="auctionEndTime">Auction End Time:</label>
                <input type="time" name="auctionEndTime" id="auctionEndTime" required>
            </div>
            <div class="form-group">
                <label>Product Type:</label>
                <div>
                    <input type="radio" name="productType" id="putForBid" value="bid" required>
                    <label for="putForBid">Put for Bid</label>
                </div>
                <div>
                    <input type="radio" name="productType" id="putForSell" value="sell">
                    <label for="putForSell">Put for Direct Sell</label>
                </div>
            </div>
            <button type="submit">List Product</button>
        </form>
    </div>
</body>
</html>
