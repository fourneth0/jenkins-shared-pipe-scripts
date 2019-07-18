
import org.fourneth.FirstTest

def hello() { 
    echo "In: to Hello world"
    def p = new FirstTest(name: 'sample');
    p.whatsTheDeal();
}