package com.buysellplatform.controller;

import com.buysellplatform.S3UploadService;
import com.buysellplatform.dao.ProductDAO;
import com.buysellplatform.model.Product;
import com.buysellplatform.model.User;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/sell")
@MultipartConfig
public class SellServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ProductDAO productDAO;
    private S3UploadService s3UploadService;
    private static final long MAX_FILE_SIZE = 1024 * 1024; // 1 MB
    private static final long MIN_FILE_SIZE = 1 * 1024; // 1 KB
    private static final String[] ALLOWED_EXTENSIONS = { "jpg", "jpeg", "pdf", "png", "heic" };

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
        s3UploadService = new S3UploadService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            String title = null, description = null, minBidPriceStr = null, auctionEndDate = null, auctionEndTime = null, fileName = null;
            InputStream inputStream = null;
            long fileSize = 0;
            String productType = null;

            for (FileItem item : multiparts) {
                if (!item.isFormField()) {
                    fileName = new File(item.getName()).getName();
                    String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

                    if (!isAllowedFileType(fileExtension)) {
                        throw new ServletException("File type not allowed. Only JPG, JPEG, PNG, PDF, and HEIC files are accepted.");
                    }

                    fileSize = item.getSize();
                    if (fileSize > MAX_FILE_SIZE || fileSize < MIN_FILE_SIZE) {
                        throw new ServletException("File size must be between 10 KB and 1 MB.");
                    }

                    inputStream = item.getInputStream();
                } else {
                    switch (item.getFieldName()) {
                        case "title":
                            title = item.getString();
                            break;
                        case "description":
                            description = item.getString();
                            break;
                        case "minBidPrice":
                            minBidPriceStr = item.getString();
                            break;
                        case "auctionEndDate":
                            auctionEndDate = item.getString();
                            break;
                        case "auctionEndTime":
                            auctionEndTime = item.getString();
                            break;
                        case "productType":
                            productType = item.getString();
                            break;
                    }
                }
            }

            if (title != null && description != null && minBidPriceStr != null && auctionEndDate != null && auctionEndTime != null && productType != null) {
                double minBidPrice = Double.parseDouble(minBidPriceStr);
                User seller = (User) request.getSession().getAttribute("user");

                if (seller == null) {
                    response.sendRedirect("login.jsp");
                    return;
                }

                // Parse auction end date and time
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                LocalDateTime auctionEndDateTime;
                try {
                    LocalDateTime date = LocalDateTime.parse(auctionEndDate + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    LocalDateTime time = LocalDateTime.parse(auctionEndTime, timeFormatter);
                    auctionEndDateTime = LocalDateTime.of(date.toLocalDate(), time.toLocalTime());
                } catch (Exception e) {
                    System.out.println("Date-time parsing failed: " + e.getMessage());
                    // Set auction end date to 24 hours from now if parsing fails
                    auctionEndDateTime = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0);
                }

                Timestamp auctionEndTimestamp = Timestamp.valueOf(auctionEndDateTime);

                // Upload file to S3
                if (inputStream != null) {
                    s3UploadService.uploadFile(fileName, inputStream, fileSize);
                } else {
                    throw new ServletException("File upload failed. No file found.");
                }

                Product product = new Product();
                product.setTitle(title);
                product.setDescription(description);
                product.setMinBidPrice(minBidPrice);
                product.setAuctionEndDate(auctionEndTimestamp);
                product.setImage(fileName);
                product.setSellerId(seller.getId());
                product.setProductType(productType);  // Set the product type

                boolean isProductListed = productDAO.listProduct(product);

                if (isProductListed) {
                    response.sendRedirect("productList");
                } else {
                    request.setAttribute("errorMessage", "Product listing failed. Please try again.");
                    request.getRequestDispatcher("sell.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("errorMessage", "All fields are required.");
                request.getRequestDispatcher("sell.jsp").forward(request, response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "File upload failed due to: " + ex.getMessage());
            request.getRequestDispatcher("sell.jsp").forward(request, response);
        }
    }

    private boolean isAllowedFileType(String fileExtension) {
        for (String ext : ALLOWED_EXTENSIONS) {
            if (ext.equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }
}
