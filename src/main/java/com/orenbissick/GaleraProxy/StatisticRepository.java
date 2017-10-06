package com.orenbissick.GaleraProxy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("statisticRepository")
public interface StatisticRepository extends JpaRepository<Statistic, String> {
}

