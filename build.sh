cd src/
mv components/* ./
javac *.java
cd ..
rm -rf build/
mkdir build
mv src/*.class build/
cp -R assets/* build/
