package com.buysellplatform.controller;

import com.buysellplatform.dao.ProductDAO;
import com.buysellplatform.model.Product;
import com.buysellplatform.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/myListedProducts")
public class MyListedProductsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the currently logged-in user
        User user = (User) request.getSession().getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        int sellerId = user.getId();
        System.out.println("Seller ID: " + sellerId); // Debug log

        List<Product> products = productDAO.getProductsBySeller(sellerId);

        // Debug log to check retrieved products
        if (products != null && !products.isEmpty()) {
            System.out.println("Number of products retrieved: " + products.size());
        } else {
            System.out.println("No products found for seller ID: " + sellerId);
        }

        // Add products to request scope
        request.setAttribute("products", products);

       
     // Forward to the JSP page
        request.getRequestDispatcher("/myListedProducts.jsp").forward(request, response);

    }
}
