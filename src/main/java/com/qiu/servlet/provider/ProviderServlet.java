package com.qiu.servlet.provider;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import com.qiu.pojo.Provider;
import com.qiu.pojo.User;
import com.qiu.service.provider.ProviderService;
import com.qiu.service.provider.ProviderServiceImp;
import com.qiu.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProviderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if(method != null && method.equals("query")){
            this.query(req,resp);
        }else if(method != null && method.equals("add")){
            this.add(req,resp);
        }else if(method != null && method.equals("view")){ //proderview.js传来的
            this.getProviderById(req,resp,"providerview.jsp");
        }else if(method != null && method.equals("modify")){
            this.getProviderById(req,resp,"providermodify.jsp");
        }else if(method != null && method.equals("modifysave")){
            this.modify(req,resp);
        }else if(method != null && method.equals("delprovider")){
            this.delProvider(req,resp);
        };
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       doGet(req, resp);
    }

    private void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //providerList里没有用分页
        String proCode = req.getParameter("queryProCode");
        String proName = req.getParameter("queryProName");
        if (StringUtils.isNullOrEmpty(proCode)) {
            proCode = "";
        }
        if (StringUtils.isNullOrEmpty(proName)) {
            proName = "";
        }

        List<Provider> providers = null;
        ProviderService providerService = new ProviderServiceImp();
        providers = providerService.getProviderList(proName, proCode);
        req.setAttribute("proCode", proCode);
        req.setAttribute("proName", proName);
        req.setAttribute("providerList", providers);
        req.getRequestDispatcher("providerlist.jsp").forward(req, resp);
    }

    private void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String proCode = req.getParameter("proCode");
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");

        Provider provider = new Provider();
        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProFax(proFax);
        provider.setProAddress(proAddress);
        provider.setProDesc(proDesc);
        provider.setCreatedBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        provider.setCreationDate(new Date());

        boolean flag = false;
        ProviderService providerService = new ProviderServiceImp();
        flag = providerService.add(provider);
        if(flag){
            resp.sendRedirect(req.getContextPath()+"/jsp/provider.do?method=query");
        }else{
            req.getRequestDispatcher("provideradd.jsp").forward(req, resp);
        }
    }

    private void getProviderById(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
        String id = req.getParameter("proid");
        if(!StringUtils.isNullOrEmpty(id)) {
            ProviderService providerService = new ProviderServiceImp();
            Provider provider = null;
            provider = providerService.getProviderById(id);
            req.setAttribute("provider", provider);
            req.getRequestDispatcher(url).forward(req, resp);
        }
    }

    private void modify(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //proCode在jsp页面设置为只读，而且在Dao的修改不涉及
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");
        String id = req.getParameter("proid");
        Provider provider = new Provider();
        provider.setProName(proName);
        provider.setId(Integer.parseInt(id));
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProFax(proFax);
        provider.setProAddress(proAddress);
        provider.setProDesc(proDesc);
        provider.setModifyBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        provider.setModifyDate(new Date());
        Boolean flag = false;
        ProviderService providerService = new ProviderServiceImp();
        flag = providerService.modify(provider);
        if (flag) {
            resp.sendRedirect("provider.do?method=query");
        } else {
            req.getRequestDispatcher("providermodify.jsp").forward(req, resp);
        }
    }

    private void delProvider(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("proid");
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(!StringUtils.isNullOrEmpty(id)){
            ProviderService providerService = new ProviderServiceImp();
            int flag = providerService.deleteProviderById(id);
            if(flag == 0){//删除成功
                resultMap.put("delResult", "true");
            }else if(flag == -1){//删除失败
                resultMap.put("delResult", "false");
            }else if(flag > 0){//该供应商下有订单，不能删除，返回订单数
                resultMap.put("delResult", String.valueOf(flag));
            }
        }else{
            resultMap.put("delResult", "notexit");
        }
        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();

    }
}

