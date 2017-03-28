package fr.jcgay.maven.profiler.reporting.json;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import fr.jcgay.maven.profiler.reporting.Files;
import fr.jcgay.maven.profiler.reporting.ReportDirectory;
import fr.jcgay.maven.profiler.reporting.Reporter;
import fr.jcgay.maven.profiler.reporting.template.Data;
import fr.jcgay.maven.profiler.reporting.template.EntryAndTime;
import fr.jcgay.maven.profiler.reporting.template.Project;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

import static fr.jcgay.maven.profiler.reporting.Format.ms;
import static fr.jcgay.maven.profiler.reporting.ReportFormat.JSON;
import static org.slf4j.LoggerFactory.getLogger;

public class JsonReporter implements Reporter {

    private static final Logger LOGGER = getLogger(JsonReporter.class);
    private  JsonObject obj = new JsonObject();

    @Override
    public void write(Data data, ReportDirectory directory) {
        String reportString = getJSONRepresentation(data);

        try {
            HttpResponse<String> response = Unirest.post("https://vacslp01dev.oss.central1.com/mdi-dev/profile-database.php")
                .header("accept", "application/json")
                .body(reportString)
                .asString();
            LOGGER.debug("************ POST REQUEST ************");
            LOGGER.debug("Status text of response: " + response.getStatusText());
        } catch (UnirestException e) {
            e.printStackTrace();
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
