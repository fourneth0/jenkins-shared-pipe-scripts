package org.fourneth

import org.kohsuke.github.*

class FirstTest { 
    String name;

    String whatsTheDeal() {
//        GitHub github = GitHub.connect('x', 'x');
        GitHub github = GitHub.connectUsingOAuth('9a58df2fa0e067f9f76d13658ed6bb2af1e55877')
        github.checkApiUrlValidity();
        github.getRepository('')
        return "Deal is ${name}"
    }
}