package com.orenbissick.GaleraProxy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("statisticService")
public class StatisticServiceImp implements StatisticService{
	@Autowired
	StatisticRepository statRepo;
	
	@Override
	public Statistic getStatisticByName(String name){
		return statRepo.findOne(name);
	}

	@Override
	public List<Statistic> getAllStatistics() {
		return statRepo.findAll();
	}

	@Override
	public Statistic saveStatistic(Statistic stats) {
		return statRepo.save(stats);
	}
}
