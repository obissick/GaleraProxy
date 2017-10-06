package com.orenbissick.GaleraProxy;

import java.util.List;

public interface StatisticService {
	Statistic getStatisticByName(String name);
	List<Statistic> getAllStatistics();
	Statistic saveStatistic(Statistic stats);
}
