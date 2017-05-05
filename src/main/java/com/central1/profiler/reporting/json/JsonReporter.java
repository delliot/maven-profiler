package com.central1.profiler.reporting.json;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.central1.profiler.reporting.Reporter;
import com.central1.profiler.reporting.template.Data;
import com.central1.profiler.reporting.template.Project;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import static com.central1.profiler.reporting.Format.ms;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * Full credit to jcgay on github for the original maven-profiler
 *
 * Licensed under MIT
 *
 * Modifications by Delan Elliot (delliot@central1.com)
 */
public class JsonReporter implements Reporter {

    private static final Logger LOGGER = getLogger(JsonReporter.class);
    private  JsonObject obj = new JsonObject();
    private String postUrl;


    public JsonReporter(String url) {
        this.postUrl = url;
    }


    @Override
    public void write(Data data) {
        String reportString = getJSONRepresentation(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            GZIPOutputStream gzos = new GZIPOutputStream(baos);

            gzos.write(reportString.getBytes("UTF-8"));
            gzos.flush();
            gzos.close();
        } catch (IOException e) {
            LOGGER.debug("GZIP error: " + e.getMessage());
        }

        byte [] zipBytes = baos.toByteArray();

        try {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(1000).build();

            HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            HttpPost post = new HttpPost(postUrl);

            post.addHeader("Content-Encoding", "gzip");
            post.addHeader("Content-Type", "application/json");

            HttpEntity e = new ByteArrayEntity(zipBytes);
            post.setEntity(e);
            HttpResponse response = client.execute(post);

            LOGGER.debug("************ POST REQUEST ************");
            LOGGER.debug("Response Status: " + response.getStatusLine());
        } catch (IOException e) {
            LOGGER.error("Request error: " + e.getMessage());
        }

    }



    private String getJSONRepresentation(Data context) {
        obj.add("key", context.getKey());
        obj.add("project_name", context.getName());
        obj.add("time", ms(context.getBuildTime()));
        obj.add("machine_name", context.getMachineName());
        obj.add("developer_name", context.getDeveloperName());
        obj.add("goals", context.getGoals());
        obj.add("date", context.getFormattedDate());
        obj.add("parameters", context.getParameters().toString());
        obj.add("operatingSystem", context.getOperatingSystem());
        obj.add("ipAddress",context.getIpAddress());
        obj.add("succeeded", ((Boolean) context.getBuildSucceeded()).toString());

        JsonArray projectsArr = new JsonArray();
        for (Project project : context.getProjects()) {
            JsonObject projectObj = new JsonObject();
            projectObj.add("project", project.getName());
            projectObj.add("time", ms(project.getTime()));

            projectsArr.add(projectObj);
        }

        obj.add("projects", projectsArr);

        return obj.toString();
    }
}
