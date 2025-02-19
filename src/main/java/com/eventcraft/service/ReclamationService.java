package com.eventcraft.service;

import com.eventcraft.model.Reclamation;
import com.eventcraft.model.User;
import com.eventcraft.dao.ReclamationDAO;

import java.util.List;

public class ReclamationService {

    private ReclamationDAO reclamationDAO = new ReclamationDAO();

    public boolean submitReclamation(User user, String titre, String description, String type) {
        if (user == null || titre == null || titre.isEmpty() || description == null || description.isEmpty() || type == null || type.isEmpty()) {
            return false;
        }

        Reclamation reclamation = new Reclamation();
        reclamation.setTitre(titre);
        reclamation.setDescription(description);
        reclamation.setDate(java.time.LocalDateTime.now());
        reclamation.setStatut("Pending");
        reclamation.setType(type);
        reclamation.setIdUser(user.getIdUser());

        return reclamationDAO.saveReclamation(reclamation);
    }

    public List<Reclamation> getReclamationsByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return reclamationDAO.getReclamationsByUserId(user.getIdUser());
    }
}
