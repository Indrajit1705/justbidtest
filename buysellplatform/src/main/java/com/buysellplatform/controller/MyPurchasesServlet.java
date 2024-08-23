package com.buysellplatform.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.buysellplatform.dao.PurchaseDAO;
import com.buysellplatform.model.Product;
import com.buysellplatform.model.User;

@WebServlet("/myPurchases")
public class MyPurchasesServlet extends HttpServlet {

    private PurchaseDAO purchaseDAO;

    @Override
    public void init() {
        purchaseDAO = new PurchaseDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<Product> purchasedProducts = purchaseDAO.getPurchasedProducts(currentUser.getId());

        request.setAttribute("purchasedProducts", purchasedProducts);
        request.getRequestDispatcher("myPurchases.jsp").forward(request, response);
    }
}

