package org.karaffe.compiler;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.karaffe.compiler.args.Flag;
import org.karaffe.compiler.args.ParameterName;
import org.karaffe.compiler.frontend.karaffe.antlr.KaraffeLexer;
import org.karaffe.compiler.frontend.karaffe.antlr.KaraffeParser;
import org.karaffe.compiler.report.Report;
import org.karaffe.compiler.tree.TreeFormatter;
import org.karaffe.compiler.util.CompilerContext;
import org.karaffe.compiler.util.KaraffeSource;
import org.karaffe.compiler.visitor.ClassNameListener;
import org.karaffe.compiler.visitor.KaraffeASTCreateVisitor;
import org.karaffe.compiler.visitor.KaraffeParserVisitor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KaraffeCompiler {
    private final CompilerContext context;

    public KaraffeCompiler(CompilerContext context) {
        this.context = context;
    }

    public void run() {
        List<KaraffeSource> sources = context.getSources();
        KaraffeParserVisitor parserVisitor = new KaraffeParserVisitor(context);
        KaraffeASTCreateVisitor createASTVisitor = new KaraffeASTCreateVisitor(context);
        for (KaraffeSource source : sources) {
            try {
                Optional<KaraffeParser.SourceFileContext> optParseContext = parse(source).filter(ctx -> ctx.EOF() != null);
                optParseContext.ifPresent(createASTVisitor::visitSourceFile);
                optParseContext.ifPresent(c -> c.accept(parserVisitor));
            } catch (SemanticAnalysisException e) {
                // ignore
            }
        }
        this.context.setAST(createASTVisitor.getCompilationUnit());

        if (context.getParameter(ParameterName.EMIT).map(p -> p.equalsIgnoreCase("ast")).orElse(false)) {
            TreeFormatter formatter = new TreeFormatter(context);
            this.context.add(Report.newInfoReport("AST Info").withBody(formatter.format(this.context.getCurrentAST())).build());
        }

        if (this.context.hasFlag(Flag.DRY_RUN)) {
            return;
        }
        for (Map.Entry<Path, byte[]> entry : context.getOutputFiles().entrySet()) {
            try {
                Files.write(entry.getKey(), entry.getValue());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private Optional<KaraffeParser.SourceFileContext> parse(KaraffeSource source) {
        try {
            KaraffeLexer lexer = new KaraffeLexer(source.asCharStream());
            lexer.removeErrorListeners();
            DefaultErrorListener errorHandler = new DefaultErrorListener(context);
            lexer.addErrorListener(errorHandler);
            CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
            KaraffeParser parser = new KaraffeParser(commonTokenStream);
            parser.setErrorHandler(new KaraffeParseErrorStrategy());
            parser.removeErrorListeners();
            parser.addParseListener(new ClassNameListener(context));
            parser.addErrorListener(errorHandler);
            if (errorHandler.hasSyntaxError()) {
                return Optional.empty();
            } else {
                return Optional.of(parser.sourceFile());
            }
        } catch (RecognitionException e) {
            return Optional.empty();
        }
    }

}
