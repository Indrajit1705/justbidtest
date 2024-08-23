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

@WebServlet("/BidNowServlet")
public class BidNowServlet extends HttpServlet {

    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            double bidAmount = Double.parseDouble(request.getParameter("bidAmount"));

            User currentUser = (User) request.getSession().getAttribute("user");

            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Validate the bid amount (should be at least +$5 from the current bid price)
            Product product = productDAO.getProductById(productId);
            if (bidAmount < product.getCurrentBidPrice() + 5) {
                request.setAttribute("errorMessage", "Bid amount must be at least $5 more than the current bid price.");
                request.getRequestDispatcher("productDetails.jsp?id=" + productId).forward(request, response);
                return;
            }

            boolean isUpdated = productDAO.updateBid(productId, currentUser.getId(), bidAmount);

            if (isUpdated) {
                response.sendRedirect("productDetails.jsp?productId=" + productId);
            } else {
                request.setAttribute("errorMessage", "Failed to place bid.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid input.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}
