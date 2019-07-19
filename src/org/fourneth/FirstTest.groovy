package org.fourneth

import org.kohsuke.github.*

class FirstTest { 
    String name;

    String whatsTheDeal() {
        GitHub github = GitHub.connect('x', 'x');
        return "Deal is ${name}"
    }
}