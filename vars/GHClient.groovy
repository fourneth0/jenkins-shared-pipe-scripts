
import org.fourneth.FirstTest

def hello() { 
    echo "In: to Hello world"
    FirstTest p = new FirstTest(name: 'sample')
    echo p.whatsTheDeal()
}