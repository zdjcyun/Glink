//根据公式进行计算
function VibratingWireData_calculateData(k,fi) {
    return  (0.000538 * k * Math.pow(fi, 2)).toFixed(10);
}
