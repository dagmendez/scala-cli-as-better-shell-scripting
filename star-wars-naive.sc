//> using scala 3.3.5

//> using dep com.lihaoyi::os-lib:0.11.4
//> using dep com.lihaoyi::requests:0.9.0
//> using dep com.lihaoyi::upickle:4.1.0

// Create, or if it exists already, clear and recreate the output dir
val dest = os.pwd / "output"

if os.exists(dest) then os.remove.all(dest)

os.makeDir(dest)

// Read the file
os.read
  .lines(os.pwd / "planets.txt")
  .foreach: url =>
    // Request the data
    val planet = requests.get(url).text()

    // Extract the name
    val name = ujson.read(planet)("name").str

    // Output the data to a json file named after the planet in the output directory
    os.write(dest / s"$name.json", planet)

    // Print the name for some user feedback
    println(name)
