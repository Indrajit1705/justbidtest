package com.buysellplatform.controller;

import com.buysellplatform.dao.BuyDAO;
import com.buysellplatform.model.Product;
import com.buysellplatform.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/BuyNowServlet")
public class BuyNowServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private BuyDAO buyDAO;

    @Override
    public void init() throws ServletException {
        buyDAO = new BuyDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            User currentUser = (User) request.getSession().getAttribute("user");

            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Store buyer's information and product details in the database
            boolean isSaved = buyDAO.savePurchaseDetails(productId, currentUser.getId());

            if (isSaved) {
                // Redirect to the success page
                response.sendRedirect("buyNowConfirmation.jsp");
            } else {
                request.setAttribute("errorMessage", "Failed to process your request.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid product ID.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}
