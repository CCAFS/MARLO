package org.cgiar.ccafs.marlo.security;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.IOException;

public class HibernateSessionFilter implements Filter {

    private SessionFactory sessionFactory;

    @Override
    public void init(FilterConfig filterConfig) {
        // Obtiene el contexto de Spring al inicializar el filtro
        this.sessionFactory = WebApplicationContextUtils
                .getRequiredWebApplicationContext(filterConfig.getServletContext())
                .getBean(SessionFactory.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();

            chain.doFilter(request, response);

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            throw new ServletException(e);
        }
    }

    @Override
    public void destroy() {
    }
}
