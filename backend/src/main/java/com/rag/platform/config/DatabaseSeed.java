package com.rag.platform.config;

import com.rag.platform.model.ResearchPaper;
import com.rag.platform.model.Role;
import com.rag.platform.model.RoleName;
import com.rag.platform.model.User;
import com.rag.platform.repository.ResearchPaperRepository;
import com.rag.platform.repository.RoleRepository;
import com.rag.platform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds roles, demo accounts, and sample papers when the database is empty enough to need it.
 */
@Configuration
public class DatabaseSeed {
  @Bean
  CommandLineRunner seedRolesAndDemoData(
      RoleRepository roleRepository,
      UserRepository userRepository,
      ResearchPaperRepository paperRepository,
      PasswordEncoder passwordEncoder
  ) {
    return args -> {
      for (RoleName rn : RoleName.values()) {
        if (roleRepository.findByName(rn).isEmpty()) {
          roleRepository.save(new Role(rn));
        }
      }

      Role researcher = roleRepository.findByName(RoleName.ROLE_RESEARCHER).orElseThrow();
      Role curator = roleRepository.findByName(RoleName.ROLE_CURATOR).orElseThrow();
      Role admin = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();

      if (userRepository.findByEmail("user@demo.com").isEmpty()) {
        User u = new User("Demo Researcher", "user@demo.com", passwordEncoder.encode("User@123"));
        u.getRoles().add(researcher);
        userRepository.save(u);
      }
      if (userRepository.findByEmail("curator@demo.com").isEmpty()) {
        User u = new User("Demo Curator", "curator@demo.com", passwordEncoder.encode("Curator@123"));
        u.getRoles().add(curator);
        userRepository.save(u);
      }
      if (userRepository.findByEmail("admin@demo.com").isEmpty()) {
        User u = new User("Demo Admin", "admin@demo.com", passwordEncoder.encode("Admin@123"));
        u.getRoles().add(admin);
        userRepository.save(u);
      }

      long paperCount = paperRepository.count();
      if (paperCount < 300) {
        String[] domains = {
            "Machine Learning",
            "Natural Language Processing",
            "Computer Vision",
            "Data Mining",
            "Software Engineering",
            "Cyber Security",
            "Cloud Computing",
            "Distributed Systems",
            "Information Retrieval",
            "Human Computer Interaction"
        };
        String[] journals = {"NeurIPS", "ICML", "ACL", "EMNLP", "CVPR", "ICCV", "KDD", "IEEE Access", "ACM TOIS", "TSE"};
        String[] keywordSets = {
            "transformer, attention, deep learning",
            "retrieval, ranking, search",
            "security, anomaly detection, network",
            "recommendation, personalization, behavior",
            "software architecture, design pattern, maintainability",
            "federated learning, privacy, optimization",
            "graph neural network, embedding, prediction",
            "cloud, autoscaling, orchestration",
            "testing, reliability, quality assurance",
            "nlp, summarization, language model"
        };

        int needed = (int) (300 - paperCount);
        for (int i = 0; i < needed; i++) {
          int idx = i % domains.length;
          int jdx = i % journals.length;
          int kdx = i % keywordSets.length;
          int year = 2010 + (i % 16);
          String title = "Research Study #" + (paperCount + i + 1) + " on " + domains[idx] + " Techniques";
          String authors = "Author " + (char) ('A' + (i % 26)) + ". Kumar, Coauthor " + (char) ('A' + ((i + 7) % 26)) + ". Rao";
          String doi = "10.1000/rag." + (paperCount + i + 1);
          String url = "https://example.org/papers/" + (paperCount + i + 1);
          String abstractText = "This paper presents a practical framework for " + domains[idx]
              + " and evaluates performance trade-offs using real-world datasets and reproducible experiments.";

          paperRepository.save(new ResearchPaper(
              title,
              authors,
              year,
              domains[idx],
              journals[jdx],
              doi,
              url,
              abstractText,
              keywordSets[kdx]
          ));
        }
      }
    };
  }
}
