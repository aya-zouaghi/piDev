package com.eventcraft.service;

import com.eventcraft.dao.DashDAO;
import com.eventcraft.model.Reponse;


import java.util.List;

public class ReponseService {

    private final DashDAO dashDAO;

    public ReponseService() {
        this.dashDAO = new DashDAO();
    }

    public List<Reponse> getReponsesByUserId(int userId) {
        return dashDAO.getReponsesByUserId(userId);
    }
    public Reponse getReponseByReclamationId(int reclamationId) {
        return dashDAO.getReponseByReclamationId(reclamationId);
    }

}
