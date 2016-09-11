package com.jpa.svc;

import com.jpa.dao.TemplateDao;
import com.template.api.TemplateService;
import com.template.model.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.yserver.core.datasource.DataSource;

import java.util.List;

@Service("templateService")
@DataSource(value="default")
@Transactional
public class TemplateServiceImpl implements TemplateService {
    @Autowired
    TemplateDao dao;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<Template> findAllCreated() {
        return dao.findAllCreated();
    }
}