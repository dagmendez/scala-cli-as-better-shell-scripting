//> using scala 3.3.5
//> using dep com.lihaoyi::os-lib:0.11.4
//> using dep com.lihaoyi::requests:0.9.0
//> using dep com.lihaoyi::upickle:4.1.0

// Create, or if it exists already, clear and recreate the output dir
val dest = os.pwd / "output"

if os.exists(dest) then os.remove.all(dest)

os.makeDir(dest)

val filename = "planets.txt"

if os.exists(os.pwd / filename) 
then
  // Read the file
  os.read.lines
    .stream(os.pwd / filename) // Stream in case it's a lot of data.
    .filter(_.nonEmpty)
    .foreach: url =>

      // Request the data
      val response = requests.get(url, check = false)
      val planet = response.text()

      // Handle failed requests
      if response.statusCode != 200 then println(s"Error getting planet at url '$url', got '${response.statusCode}'.")
      else
        
        val json = ujson.read(planet).objOpt
        
        // Handle the failed reads in case the paylod is not a JsonObject
        if json.isEmpty then println(s"Received answer is not a JsonObject at url '$url'!")
        else 
          
          // Extract the name and handle the missing field
          val name = json.get.get("name")
          
          if name.isEmpty then println(s"Unnamed planet at url '$url'!")
           
          // Output the data to a json file named after the planet in the output directory
          else 
            os.write
            // Write over existing files, instead of erroring.
              .over(dest / s"${name.get.str}.json", planet) 
          
            // Print the name for some user feedback
            println(name.get.str)

else println(s"Could not find the expected file '$filename', in the working directory.")
