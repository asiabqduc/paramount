package net.paramount.controller.mvp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.util.Messages;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.FileCopyUtils;

import com.github.adminfaces.template.exception.BusinessException;

import net.paramount.dmx.repository.GlobalDmxRepository;
import net.paramount.exceptions.MspDataException;
import net.paramount.exceptions.ResourcesException;
import net.paramount.framework.async.Asynchronous;
import net.paramount.framework.controller.BaseController;
import net.paramount.framework.model.ExecutionContext;
import net.paramount.msp.async.AsyncExtendedDataLoader;
import net.paramount.msp.components.ResourcesServicesHelper;
import net.paramount.msp.faces.model.Entity;

@Named(value="virtualSimulator")
@ViewScoped
public class VirtualSimulatorPage extends BaseController {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4917742028352793276L;
		private List<String> allCities;
    private List<String> allTalks;
    private Entity entity;

    @Inject
    private ResourceLoader resourceLoader;

    @Inject
  	private ApplicationContext applicationContext;

  	@Inject
  	private TaskExecutor asyncExecutor;
    
  	@Inject
  	private GlobalDmxRepository globalDmxRepository;

  	@Inject
  	private ResourcesServicesHelper resourcesServicesHelper;

  	
  	@Override
    public void doPostConstruct() {
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
        archiveData();
        //loadResourceData();
        //loadingAsyncData();
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

    protected void loadResourceData() {
      try
      {
      	Resource resource = this.resourceLoader.getResource("classpath:/data/marshall/develop_data.zip");
        InputStream inputStream = resource.getInputStream();

        byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
          //String data = new String(bdata, StandardCharsets.UTF_8);
        System.out.println(bdata);
      } 
      catch (IOException e) 
      {
      	e.printStackTrace();
          //LOGGER.error("IOException", e);
      }
    }

  	protected void loadingAsyncData() {
  		ExecutionContext executionContext = null;
  		Asynchronous asyncExtendedDataLoader = null;
  		try {
  			executionContext = ExecutionContext.builder().build();
  			executionContext.context("AA", "xx").context("DD", "ss");

  			asyncExtendedDataLoader = applicationContext.getBean(AsyncExtendedDataLoader.class, executionContext);
  			this.asyncExecutor.execute(asyncExtendedDataLoader);
  		} catch (Exception e) {
  			//log.error(e.getMessage());
  		}
  	}

  	public void archiveData() {
  		File resourceFile = null;
  		try {
    		resourceFile = resourcesServicesHelper.loadClasspathResourceFile("data/marshall/develop_data.zip");
				globalDmxRepository.archiveResourceData(resourceFile);
			} catch (MspDataException | ResourcesException e) {
				log.error(e);
			}
  	}
}
