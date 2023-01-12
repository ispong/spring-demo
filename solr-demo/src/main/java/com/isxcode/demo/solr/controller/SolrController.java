package com.isxcode.demo.solr.controller;

import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/solr")
@RequiredArgsConstructor
public class SolrController {

    private final SolrClient solrClient;

    @GetMapping("/addDoc")
    public void addDoc() {

        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.addField("username", "jack");
        solrInputDocument.addField("age", 17);
        try {
            solrClient.add(solrInputDocument);
            solrClient.commit();
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/searchDoc")
    public void searchDoc() {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.set("q", "username:*");

        try {
            QueryResponse query = solrClient.query(solrQuery);
            SolrDocumentList results = query.getResults();
            results.forEach(
                    e -> {
                        System.out.println("username:" + e.get("username"));
                        System.out.println("age:" + e.get("age"));
                    });
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
