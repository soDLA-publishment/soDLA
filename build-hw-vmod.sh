cp tree.make hw &&
cd hw &&
./tools/bin/tmake -build vmod &&
cd .. &&
rm -rf vsrc/small/vmod &&
cp -r hw/outdir/nv_small/vmod vsrc/small &&

cd hw &&
sed -i -e 's/nv_small/nv_large/' tree.make &&
./tools/bin/tmake -build vmod &&
cd .. &&
rm -rf vsrc/large/vmod &&
cp -r hw/outdir/nv_large/vmod vsrc/large
