package com.buysellplatform.controller;

import com.buysellplatform.dao.ProductDAO;
import com.buysellplatform.dao.BidDAO;
import com.buysellplatform.model.Product;
import com.buysellplatform.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/productDetails")
public class ProductDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ProductDAO productDAO;
    private BidDAO bidDAO;

    @Override
    public void init() throws ServletException {
        // Initialize the DAO objects
        productDAO = new ProductDAO();
        bidDAO = new BidDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        System.out.println("Received product ID: " + idParam);

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("error.jsp?message=Product ID is missing");
            return;
        }

        try {
            int productId = Integer.parseInt(idParam);
            System.out.println("Converted product ID to integer: " + productId);

            Product product = productDAO.getProductDetails(productId);
            System.out.println("Product retrieved from DAO: " + product);

            if (product == null) {
                System.out.println("Product is null, redirecting to error page.");
                response.sendRedirect("error.jsp?message=Product not found");
                return;
            }

            // Set the product as a request attribute and forward to the JSP
            request.setAttribute("product", product);
            request.getRequestDispatcher("productDetails.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: " + e.getMessage());
            response.sendRedirect("error.jsp?message=Invalid Product ID format");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("error.jsp?message=An error occurred while retrieving product details");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            double bidPrice = Double.parseDouble(request.getParameter("bidPrice"));

            User currentUser = (User) request.getSession().getAttribute("user");

            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            Product product = productDAO.getProductDetails(productId);

            if (product == null) {
                request.setAttribute("errorMessage", "Product not found.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            if (bidPrice < product.getCurrentBidPrice() + 5) {
                request.setAttribute("errorMessage", "Your bid must be at least $5 higher than the current bid.");
                request.setAttribute("product", product);
                request.getRequestDispatcher("productDetails.jsp").forward(request, response);
                return;
            }

            boolean isBidPlaced = bidDAO.updateBid(productId, currentUser.getId(), bidPrice);

            if (isBidPlaced) {
                // Debugging output
                System.out.println("Bid successfully placed. Redirecting to bidConfirmation.jsp.");
                response.sendRedirect("bidConfirmation.jsp");
            } else {
                request.setAttribute("errorMessage", "Failed to place the bid. Please try again.");
                request.setAttribute("product", product);
                request.getRequestDispatcher("productDetails.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid bid price.");
            request.getRequestDispatcher("productDetails.jsp?id=" + request.getParameter("productId")).forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

}
