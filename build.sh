javac -d build com/matthewkayin/ray/*.java
cd build
jar cfe ray.jar com.matthewkayin.ray.Main com/matthewkayin/ray/*.class
cd ../
rm -f ray.jar
mv build/ray.jar ray.jar
