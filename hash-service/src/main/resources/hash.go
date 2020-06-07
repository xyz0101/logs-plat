

package main

import (
	"fmt"
	"hash/fnv"
	"os"
	"strconv"

)
func testHash(val string,max string) int32 {
	hasher := fnv.New32a()
	hasher.Write([]byte(val))
	hash := hasher.Sum32()
	//fmt.Println(hash)
	p := int32(hash)
	//fmt.Println(p)
	if p < 0 {
		p = -p
	}
        maxInt,error := strconv.Atoi(max)
	if error != nil{
		fmt.Println("字符串转换成整数失败")
	}
	i := p % int32(maxInt)
	//fmt.Println(i)
	return i;
}

func main() {

val := testHash(os.Args[1],os.Args[2])
fmt.Println(val)

}



