package net.paramount.msp.controller.mvp;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.util.Messages;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;

import com.github.adminfaces.template.exception.BusinessException;

import net.paramount.common.ListUtility;
import net.paramount.framework.async.Asynchronous;
import net.paramount.framework.model.ExecutionContext;
import net.paramount.msp.async.AsyncExtendedDataLoader;
import net.paramount.msp.faces.model.Entity;

@Named(value="virtualSimulator")
@ViewScoped
public class VirtualSimulatorPage implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4917742028352793276L;
		private List<String> allCities;
    private List<String> allTalks;
    private Entity entity;

  	@Inject
  	private ApplicationContext applicationContext;

  	@Inject
  	private TaskExecutor asyncExecutor;
    
    @PostConstruct
    public void init() {
        allCities = Arrays.asList("São Paulo", "New York", "Tokyo", "Islamabad", "Chongqing", "Guayaquil", "Porto Alegre", "Hanoi", "Montevideo", "Shijiazhuang", "Guadalajara","Stockholm",
                "Seville", "Moscow", "Glasgow", "Reykjavik", "Lyon", "Barcelona", "Kieve", "Vilnius", "Warsaw", "Budapest", "Prague", "Sofia", "Belgrade");
        allTalks = Arrays.asList("Designing for Modularity with Java 9", "Twelve Ways to Make Code Suck Less", "Confessions of a Java Educator: 10 Java Insights I Wish I’d Had Earlier",
                "Ten Simple Rules for Writing Great Test Cases", "No more mocks, only real tests with Arquillian", "Cloud native Java with JakartaEE and MicroProfile","Jenkins Essentials: an evergreen version of Jenkins",
                "From Java EE to Jakarta EE: a user perspective", "Cloud Native Java with Open J9, Fast, Lean and Definitely Mean", "Service Mesh and Sidecars with Istio and Envoy");
        allCities.sort(Comparator.naturalOrder());
        allTalks.sort(Comparator.naturalOrder());
        entity = new Entity();
    }

    public void clear() {
        entity = new Entity();
        loadingAsyncData();
    }

    public void remove() {
        Messages.create("Info").detail("Entity removed successfully.").add();
        clear();
    }

    public void save() {
        BusinessException be = new BusinessException();
        if(entity.getFirstname().trim().length() < 5) {
            be.addException(new BusinessException("Firstname must have at least 5 characters", FacesMessage.SEVERITY_ERROR, "firstname"));
        }
        
        if(entity.getAge() < 18) {
            be.addException(new BusinessException("<b>Age</b> must be greater or equal to <b style=\"color:#fff\">18</b>", FacesMessage.SEVERITY_ERROR, "age"));
        }
        
        be.build(); //will throw exceptions if has any enqueued
        
        Messages.create("Info").detail(String.format("Entity %s successfully.",isNew() ? "created":"updated")).add();
        if(isNew()) {
            entity.setId(new Random(Instant.now().getEpochSecond()).nextLong());
        }
    }

    public List<String> completeTalk(String query) {
        List<String> result = new ArrayList<>();
        allTalks.stream().filter(t -> t.toLowerCase().contains(query.toLowerCase()))
                       .forEach(result::add);
        return result;
    }

    public Boolean isNew() {
        return entity.getId() == null;
    }

    public List<String> getCities() {
        return allCities;
    }

    public void setCities(List<String> cities) {
        this.allCities = cities;
    }

    public List<String> getTalks() {
        return allTalks;
    }

    public void setTalks(List<String> talks) {
        this.allTalks = talks;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }


  	protected void loadingAsyncData() {
  		Map<String, Object> executorMap = ListUtility.createMap();
  		ExecutionContext executionContext = null;
  		Asynchronous asyncExtendedDataLoader = null;
  		Asynchronous asyncDataPackageLoader = null;
  		try {
  			executionContext = ExecutionContext.builder().build().context("AA", "xx").context("DD", "ss");

  			asyncExtendedDataLoader = applicationContext.getBean(AsyncExtendedDataLoader.class, executionContext);
  			this.asyncExecutor.execute(asyncExtendedDataLoader);

  			executorMap.put("asyncExtendedDataLoader", asyncExtendedDataLoader);
  			executorMap.put("asyncDataPackageLoader", asyncDataPackageLoader);
  		} catch (Exception e) {
  			//log.error(e.getMessage());
  		}
  	}
}