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

      if (paperRepository.count() == 0) {
        paperRepository.save(new ResearchPaper(
            "Attention Is All You Need",
            "Vaswani et al.",
            2017,
            "Machine Learning",
            "NeurIPS",
            "10.5555/3295222.3295349",
            "https://arxiv.org/abs/1706.03762",
            "Transformer architecture for sequence modeling.",
            "transformer, attention, nlp"
        ));
        paperRepository.save(new ResearchPaper(
            "BERT: Pre-training of Deep Bidirectional Transformers",
            "Devlin et al.",
            2019,
            "Natural Language Processing",
            "NAACL",
            "10.18653/v1/N19-1423",
            "https://arxiv.org/abs/1810.04805",
            "Bidirectional encoder representations from transformers.",
            "bert, language model, nlp"
        ));
        paperRepository.save(new ResearchPaper(
            "Deep Residual Learning for Image Recognition",
            "He et al.",
            2016,
            "Computer Vision",
            "CVPR",
            "10.1109/CVPR.2016.90",
            "https://arxiv.org/abs/1512.03385",
            "Residual networks enable very deep CNN training.",
            "resnet, deep learning, vision"
        ));
      }
    };
  }
}
