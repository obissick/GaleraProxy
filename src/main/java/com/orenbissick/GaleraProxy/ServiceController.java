package com.orenbissick.GaleraProxy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceController {
	@Autowired
	private StatisticService statService;
	
	@RequestMapping(value = "/stats", method = RequestMethod.GET)
	public List<Statistic> getStatistics(){
		return statService.getAllStatistics();	
	}
	
    @RequestMapping(value = "/stats/{id}", method = RequestMethod.GET)
    public Statistic getStatistic(@PathVariable("id") String name) {
		return statService.getStatisticByName(name);
	}
    
    @RequestMapping(value = "stats", method = RequestMethod.POST)
    public String saveStatistic(Statistic stat){
        statService.saveStatistic(stat);
        return "redirect:/stats/" + stat.getName();
    }
}
