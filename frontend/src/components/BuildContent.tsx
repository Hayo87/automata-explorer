import { Stats } from "./CytoscapeCanvas";

/**
 * @file BuildContent.tsx
 * 
 * Provides and processes the content used in the build result modal. It includes loading the meta information as well as
 * calculation of some simple build statistics. 
 */

interface BuildInfoProps {
  stats: Stats
  reference: string;
  subject: string;
}

const BuildInfo: React.FC<BuildInfoProps> = ({ reference, subject, stats }) => {

    return (
        <div>
            <h2><center>Build Result</center></h2>
            <hr></hr>
            <div className = 'buildInfo'>
                <div className = 'buildInfo-column'>
                    <br></br>
                    <strong>Inputs</strong>
                    <div className = 'reference-file-name'><p>Reference: {reference} </p></div>
                    <div className = 'subject-file-name'><p>Subject: {subject} </p></div>
                    <br></br>

                    <strong>Matches </strong>
                    <p>Full: {stats.unchangedEdges} </p>
                    {stats.combinedEdges !== 0 && (<p>Partial: {stats.combinedEdges}</p>)}
                    <p>No match: {stats.totalEdges - stats.unchangedEdges - stats.combinedEdges }</p>
                    <br></br>

                    <strong>Metadata</strong>
                    <p>Number of Nodes: {stats.totalNodes} </p>
                    <p>Number of Edges: {stats.totalEdges}</p>
                </div>
                <div className = "buildInfo-column">
                    <p className="percentage">{(((stats.unchangedEdges + stats.combinedEdges) / stats.totalEdges) * 100).toFixed(0)} %</p>
                </div>    
            </div>
        </div>
    )
}

export default BuildInfo;