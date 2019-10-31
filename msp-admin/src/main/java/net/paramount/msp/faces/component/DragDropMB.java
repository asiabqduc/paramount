/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.paramount.msp.faces.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import javax.faces.view.ViewScoped;
import org.primefaces.event.DragDropEvent;

import net.paramount.msp.model.Car;
import net.paramount.msp.service.CarService;

/**
 *
 * @author rafael-pestano
 */
@Named
@ViewScoped
public class DragDropMB implements Serializable {
    
    @Inject
    private CarService service;
 
    private List<Car> cars;
     
    private List<Car> droppedCars;
     
    private Car selectedCar;
     
    @PostConstruct
    public void init() {
        cars = service.createCars(9);
        droppedCars = new ArrayList<>();
    }
     
    public void onCarDrop(DragDropEvent ddEvent) {
        Car car = ((Car) ddEvent.getData());
  
        droppedCars.add(car);
        cars.remove(car);
    }
     
    public void setService(CarService service) {
        this.service = service;
    }
 
    public List<Car> getCars() {
        return cars;
    }
 
    public List<Car> getDroppedCars() {
        return droppedCars;
    }    
 
    public Car getSelectedCar() {
        return selectedCar;
    }
 
    public void setSelectedCar(Car selectedCar) {
        this.selectedCar = selectedCar;
    }
    
}
