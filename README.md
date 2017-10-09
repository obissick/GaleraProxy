# GaleraProxy
Simple Proxy Server for checking status of galera and routing traffic.

Specific usage
--

__Usage:__ Run Archive

	java -jar GaleraProxy.jar 

__Example Config:__ Place config in /etc/galeraproxy/settings.properties     

    #Galera node IP's
    fip=10.1.10.220,10.1.10.221,10.1.10.222
    #Galera port
    fport=3306
    #Galera DB user
    dbuser=
    #Galera DB password
    dbpassword=
    #Port to run proxy
    lport=3307
    ltype=round-robin

__Usage:__ API http://localhost:8080/stats
    
    [
      {
        "name": "10.1.10.220",
        "totalConnections": 0,
        "currConnections": 6,
        "status": "4"
      },
      {
        "name": "10.1.10.221",
        "totalConnections": 0,
        "currConnections": 4,
        "status": "4"
      },
      {
        "name": "10.1.10.222",
        "totalConnections": 0,
        "currConnections": 4,
        "status": "4"
      }
    ]
